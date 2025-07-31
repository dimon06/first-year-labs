#pragma once

#include "dynamic-buffer.h"

#include <algorithm>
#include <cassert>
#include <memory>
#include <new>
#include <type_traits>
#include <utility>

namespace ct {
template <typename T, std::size_t SMALL_SIZE>
class SocowVector {
public:
  using ValueType = T;

  using Reference = T&;
  using ConstReference = const T&;

  using Pointer = T*;
  using ConstPointer = const T*;

  using Iterator = Pointer;
  using ConstIterator = ConstPointer;

  static_assert(std::is_copy_constructible_v<T>, "T must have a copy constructor");
  static_assert(std::is_nothrow_move_constructible_v<T>, "T must have a non-throwing move constructor");
  static_assert(std::is_copy_assignable_v<T>, "T must have a copy assignment operator");
  static_assert(std::is_nothrow_move_assignable_v<T>, "T must have a non-throwing move assignment operator");
  static_assert(std::is_nothrow_swappable_v<T>, "T must have a non-throwing swap");

  static_assert(SMALL_SIZE > 0, "SMALL_SIZE must be positive");

private:
  bool is_small() const noexcept {
    return is_small_;
  }

  bool is_large() const noexcept {
    return !is_small();
  }

  bool unshare(size_t new_size) {
    if (is_small() || dynamic_data_->cnt_reference() == 1) {
      return false;
    }
    size_t cap = dynamic_data_->size_buffer();
    DynamicBuffer<T>* new_buf = DynamicBuffer<T>::create(cap);
    try {
      std::uninitialized_copy_n(dynamic_data_->data(), new_size, new_buf->data());
    } catch (...) {
      new_buf->destroy();
      throw;
    }
    dynamic_data_->dec_ref();
    dynamic_data_ = new_buf;
    return true;
  }

  bool unshare() {
    return unshare(size());
  }

public:
  SocowVector() noexcept
      : dynamic_data_(nullptr) {}

  SocowVector(const SocowVector& other)
      : size_(other.size_)
      , is_small_(other.is_small_) {
    if (is_small()) {
      std::uninitialized_copy_n(other.static_data_, size_, static_data_);
    } else {
      dynamic_data_ = other.dynamic_data_;
      dynamic_data_->inc_ref();
    }
  }

  SocowVector(SocowVector&& other) noexcept
      : size_(other.size_)
      , is_small_(other.is_small_) {
    if (is_small_) {
      std::uninitialized_move_n(other.static_data_, size_, static_data_);
      std::destroy_n(other.static_data_, other.size_);
    } else {
      dynamic_data_ = other.dynamic_data_;
      other.dynamic_data_ = nullptr;
    }
    other.size_ = 0;
    other.is_small_ = true;
  }

  SocowVector& operator=(const SocowVector& other) {
    if (this == &other) {
      return *this;
    }
    SocowVector tmp(other);
    *this = std::move(tmp);
    return *this;
  }

  SocowVector& operator=(SocowVector&& other) noexcept {
    if (this == &other) {
      return *this;
    }
    if (is_small_) {
      std::destroy_n(static_data_, size());
    } else if (dynamic_data_ != nullptr) {
      if (dynamic_data_->cnt_reference() == 1) {
        std::destroy_n(dynamic_data_->data(), size());
        dynamic_data_->destroy();
      } else {
        dynamic_data_->dec_ref();
      }
    }

    size_ = other.size_;
    is_small_ = other.is_small_;
    if (is_small()) {
      std::uninitialized_move_n(other.static_data_, size(), static_data_);
      std::destroy_n(other.static_data_, size());
    } else {
      dynamic_data_ = other.dynamic_data_;
      other.dynamic_data_ = nullptr;
    }
    other.size_ = 0;
    other.is_small_ = true;
    return *this;
  }

  ~SocowVector() noexcept {
    if (is_small()) {
      std::destroy_n(static_data_, size_);
    } else {
      if (dynamic_data_) {
        if (dynamic_data_->cnt_reference() == 1) {
          std::destroy_n(dynamic_data_->data(), size_);
          dynamic_data_->destroy();
        } else {
          dynamic_data_->dec_ref();
          dynamic_data_ = nullptr;
        }
      }
    }
  }

private:
  void swap_data(SocowVector& other) noexcept {
    size_t common = std::min(size(), other.size());
    std::uninitialized_move_n(other.static_data_ + common, other.size() - common, static_data_ + common);
    std::destroy_n(other.static_data_ + common, other.size() - common);
  }

public:
  void swap(SocowVector& other) noexcept {
    if (this == &other) {
      return;
    }
    if (is_large() && other.is_large()) {
      std::swap(size_, other.size_);
      std::swap(dynamic_data_, other.dynamic_data_);
    } else if (is_small() && other.is_small()) {
      size_t common = std::min(size(), other.size());
      for (size_t i = 0; i < common; ++i) {
        std::swap(static_data_[i], other.static_data_[i]);
      }
      if (size() < other.size()) {
        swap_data(other);
      } else if (size() > other.size()) {
        other.swap_data(*this);
      }
      std::swap(size_, other.size_);
    } else {
      SocowVector& small = is_large() ? other : *this;
      SocowVector& big = is_large() ? *this : other;
      DynamicBuffer<T>* tmp = big.dynamic_data_;
      std::uninitialized_move_n(small.static_data_, small.size(), big.static_data_);
      std::destroy_n(small.static_data_, small.size_);
      small.dynamic_data_ = tmp;
      small.is_small_ = false;
      big.is_small_ = true;
      std::swap(size_, other.size_);
    }
  }

  size_t size() const noexcept {
    return size_;
  }

  size_t capacity() const noexcept {
    if (is_small()) {
      return SMALL_SIZE;
    }
    return dynamic_data_->size_buffer();
  }

  bool empty() const noexcept {
    return (size() == 0);
  }

  Reference operator[](std::size_t index) {
    unshare();
    return data()[index];
  }

  ConstReference operator[](std::size_t index) const noexcept {
    return data()[index];
  }

  Reference front() {
    return (*this)[0];
  }

  ConstReference front() const noexcept {
    return (*this)[0];
  }

  Reference back() {
    return (*this)[size() - 1];
  }

  ConstReference back() const noexcept {
    return (*this)[size() - 1];
  }

  Pointer data() {
    unshare();
    if (is_small()) {
      return static_data_;
    }
    return dynamic_data_->data();
  }

  ConstPointer data() const noexcept {
    if (is_small()) {
      return static_data_;
    }
    return dynamic_data_->data();
  }

  Iterator begin() {
    return data();
  }

  ConstIterator begin() const noexcept {
    return data();
  }

  Iterator end() {
    return begin() + size();
  }

  ConstIterator end() const noexcept {
    return begin() + size();
  }

  template <typename U>
  void emplace_back(U&& value) {
    size_t cur_size = size();
    if (size() < capacity() && (is_small() || dynamic_data_->cnt_reference() == 1)) {
      new (data() + size()) T(std::forward<U>(value));
      ++size_;
      return;
    }
    size_t new_cap = (size() == capacity() ? 2 * size() : capacity());
    DynamicBuffer<T>* new_buf = DynamicBuffer<T>::create(new_cap);
    try {
      new (new_buf->data() + size()) T(std::forward<U>(value));
    } catch (...) {
      new_buf->destroy();
      throw;
    }

    if (is_small() || dynamic_data_->cnt_reference() == 1) {
      std::uninitialized_move(begin(), end(), new_buf->data());
    } else {
      try {
        std::uninitialized_copy(std::as_const(*this).begin(), std::as_const(*this).end(), new_buf->data());
      } catch (...) {
        (new_buf->data() + size())->~T();
        new_buf->destroy();
        throw;
      }
    }
    if (is_small()) {
      std::destroy_n(static_data_, cur_size);
    } else {
      if (dynamic_data_->cnt_reference() == 1) {
        std::destroy_n(dynamic_data_->data(), cur_size);
        dynamic_data_->destroy();
      } else {
        dynamic_data_->dec_ref();
      }
    }
    dynamic_data_ = new_buf;
    is_small_ = false;
    size_ = cur_size + 1;
  }

  void push_back(const T& v) {
    emplace_back(v);
  }

  void push_back(T&& v) {
    emplace_back(std::move(v));
  }

  template <typename U>
  Iterator insert(ConstIterator pos, U&& value) {
    size_t index = pos - (is_small() ? static_data_ : dynamic_data_->data());
    push_back(std::forward<U>(value));
    for (size_t i = size() - 1; i > index; --i) {
      std::swap(data()[i], data()[i - 1]);
    }
    return data() + index;
  }

  void pop_back() {
    erase(std::as_const(*this).end() - 1);
  }

  Iterator erase(ConstIterator pos) {
    return erase(pos, pos + 1);
  }

  Iterator erase(ConstIterator first, ConstIterator last) {
    ConstIterator beg = std::as_const(*this).begin();
    size_t start = first - beg;
    size_t finish = last - beg;
    size_t count = finish - start;
    if (start >= size() || start >= finish || count == 0) {
      return const_cast<Iterator>(beg + start);
    }
    if (is_small() || (is_large() && dynamic_data_->cnt_reference() == 1)) {
      for (size_t i = 0; i < size_ - finish; ++i) {
        std::swap(data()[start + i], data()[finish + i]);
      }
      std::destroy_n(end() - count, count);
      size_ -= count;
    } else {
      SocowVector tmp(size_ == capacity() ? capacity() * 2 : capacity());
      if (tmp.capacity() > SMALL_SIZE) {
        tmp.is_small_ = false;
      }
      std::uninitialized_copy_n(std::as_const(*this).data(), start, tmp.data());
      tmp.size_ = start;
      std::uninitialized_copy_n(std::as_const(*this).data() + finish, size_ - finish, tmp.data() + start);
      tmp.size_ = size_ - count;
      *this = tmp;
    }
    return data() + start;
  }

  void clear() noexcept {
    if (is_small()) {
      std::destroy_n(static_data_, size_);
    } else {
      if (dynamic_data_->cnt_reference() == 1) {
        std::destroy_n(dynamic_data_->data(), size_);
      } else {
        dynamic_data_->dec_ref();
        dynamic_data_ = nullptr;
        is_small_ = true;
      }
    }
    size_ = 0;
  }

private:
  void capacity_change(size_t new_capacity) {
    if (new_capacity <= SMALL_SIZE) {
      DynamicBuffer<T>* old = dynamic_data_;
      dynamic_data_ = nullptr;
      if (old->cnt_reference() == 1) {
        std::uninitialized_move_n(old->data(), size(), static_data_);
        std::destroy_n(old->data(), size());
        old->destroy();
      } else {
        try {
          std::uninitialized_copy_n(old->data(), size(), static_data_);
        } catch (...) {
          dynamic_data_ = old;
          throw;
        }
        old->dec_ref();
      }
      is_small_ = true;
    } else {
      DynamicBuffer<T>* new_buf = DynamicBuffer<T>::create(new_capacity);
      if (is_small()) {
        std::uninitialized_move_n(static_data_, size(), new_buf->data());
        std::destroy_n(static_data_, size());
      } else {
        if (dynamic_data_->cnt_reference() == 1) {
          std::uninitialized_move_n(dynamic_data_->data(), size(), new_buf->data());
          std::destroy_n(dynamic_data_->data(), size());
          dynamic_data_->destroy();
        } else {
          try {
            std::uninitialized_copy_n(dynamic_data_->data(), size_, new_buf->data());
          } catch (...) {
            new_buf->destroy();
            throw;
          }
          dynamic_data_->dec_ref();
        }
      }
      is_small_ = false;
      dynamic_data_ = new_buf;
    }
  }

public:
  void reserve(size_t new_capacity) {
    if (new_capacity <= size()) {
      return;
    }
    if (new_capacity <= capacity()) {
      if (is_large() && dynamic_data_->cnt_reference() > 1) {
        shrink_to_fit();
      }
      return;
    }
    if (new_capacity > capacity()) {
      capacity_change(new_capacity);
    }
  }

  void shrink_to_fit() {
    if (is_small()) {
      return;
    }
    if (size() < capacity()) {
      capacity_change(size());
    }
  }

private:
  size_t size_ = 0;
  bool is_small_ = true;

  union {
    DynamicBuffer<T>* dynamic_data_;
    T static_data_[SMALL_SIZE];
  };

  explicit SocowVector(size_t new_capacity) {
    if (new_capacity <= SMALL_SIZE) {
      is_small_ = true;
    } else {
      is_small_ = false;
      dynamic_data_ = DynamicBuffer<T>::create(new_capacity);
    }
  }
};
} // namespace ct
