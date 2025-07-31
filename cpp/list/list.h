#pragma once

#include <cstddef>
#include <iterator>
#include <utility>

namespace ct {

template <typename T>
class List {
private:
  size_t NPOS = static_cast<size_t>(-1);

  class Base_Node {
    Base_Node* prev_;
    Base_Node* next_;

    Base_Node(Base_Node* prev = nullptr, Base_Node* next = nullptr)
        : prev_(prev ? prev : this)
        , next_(next ? next : this) {}

    friend class List;

  public:
    Base_Node* next() const {
      return next_;
    }

    Base_Node* prev() const {
      return prev_;
    }
  };

  class Node : Base_Node {
    T value_;

    template <typename U>
    Node(U&& value, Base_Node* prev, Base_Node* next)
        : Base_Node(prev, next)
        , value_(std::forward<U>(value)) {}

    friend class List;

  public:
    T& value() {
      return value_;
    }

    const T& value() const {
      return value_;
    }
  };

private:
  template <typename M>
  class My_Iterator {
  public:
    using value_type = T;
    using difference_type = std::ptrdiff_t;
    using pointer = M*;
    using reference = M&;
    using iterator_category = std::bidirectional_iterator_tag;

    template <typename U, std::enable_if_t<std::is_same_v<M, const U>, int> = 0>
    My_Iterator(const My_Iterator<U>& other) noexcept
        : elem_(other.elem()) {}

    My_Iterator() = default;

    My_Iterator(const My_Iterator& other) = default;

    My_Iterator& operator=(const My_Iterator& other) = default;

    void swap(My_Iterator& other) noexcept {
      using std::swap;
      swap(elem_, other.elem_);
    }

    reference operator*() const {
      return static_cast<Node*>(elem_)->value();
    }

    pointer operator->() const {
      return &static_cast<Node*>(elem_)->value();
    }

    My_Iterator& operator++() {
      elem_ = elem_->next();
      return *this;
    }

    My_Iterator operator++(int) {
      My_Iterator tmp = *this;
      ++*this;
      return tmp;
    }

    My_Iterator& operator--() {
      elem_ = elem_->prev();
      return *this;
    }

    My_Iterator operator--(int) {
      My_Iterator tmp = *this;
      --*this;
      return tmp;
    }

    bool operator==(const My_Iterator& other) const = default;

    Base_Node* elem() const {
      return elem_;
    }

  private:
    My_Iterator(Base_Node* node)
        : elem_(node) {}

    Base_Node* elem_;

    friend class List;

    template <typename U>
    friend class My_Iterator;
  };

public:
  using ValueType = T;

  using Reference = T&;
  using ConstReference = const T&;

  using Pointer = T*;
  using ConstPointer = const T*;

  using Iterator = My_Iterator<T>;
  using ConstIterator = My_Iterator<const T>;

  using ReverseIterator = std::reverse_iterator<Iterator>;
  using ConstReverseIterator = std::reverse_iterator<ConstIterator>;

public:
  // O(1), nothrow
  List() noexcept
      : end_(Base_Node())
      , size_(0) {
    end_.next_ = &end_;
    end_.prev_ = &end_;
  }

  // O(n), strong
  List(const List& other)
      : List(other.begin(), other.end()) {}

  // O(1), nothrow
  List(List&& other) noexcept
      : end_()
      , size_(0) {
    if (!other.empty()) {
      swap_links(other);
    } else {
      end_.next_ = &end_;
      end_.prev_ = &end_;
    }
  }

  // O(n), strong
  template <std::input_iterator InputIt>
  List(InputIt first, InputIt last)
      : List() {
    while (first != last) {
      push_back(*first);
      ++first;
    }
  }

  // O(n), strong
  List& operator=(const List& other) {
    if (this != &other) {
      List tmp(other);
      swap(*this, tmp);
    }
    return *this;
  }

  // O(this->size()), nothrow
  List& operator=(List&& other) noexcept {
    if (this != &other) {
      List tmp(std::move(other));
      swap(*this, tmp);
    }
    return *this;
  }

  // O(n), nothrow
  ~List() noexcept {
    clear();
  }

  // O(1), nothrow
  bool empty() const noexcept {
    return end_.next() == &end_;
  }

  // O(n), nothrow
  std::size_t size() const noexcept {
    if (size_ == NPOS) {
      size_ = std::distance(begin(), end());
    }
    return size_;
  }

  // O(1), nothrow
  T& front() {
    return static_cast<Node*>(end_.next())->value();
  }

  // O(1), nothrow
  const T& front() const {
    return static_cast<Node*>(end_.next())->value();
  }

  // O(1), nothrow
  T& back() {
    return static_cast<Node*>(end_.prev())->value();
  }

  // O(1), nothrow
  const T& back() const {
    return static_cast<Node*>(end_.prev())->value();
  }

  // O(1), strong
  void push_front(const T& x) {
    insert(begin(), x);
  }

  // O(1), strong
  void push_front(T&& x) {
    insert(begin(), std::move(x));
  }

  // O(1), strong
  void push_back(const T& x) {
    insert(end(), x);
  }

  // O(1), strong
  void push_back(T&& x) {
    insert(end(), std::move(x));
  }

  // O(1), nothrow
  void pop_front() {
    erase(begin());
  }

  // O(1), nothrow
  void pop_back() {
    erase(--end());
  }

  // O(1), nothrow
  Iterator begin() noexcept {
    return Iterator(end_.next());
  }

  // O(1), nothrow
  ConstIterator begin() const noexcept {
    return ConstIterator(end_.next());
  }

  // O(1), nothrow
  Iterator end() noexcept {
    return Iterator(&end_);
  }

  // O(1), nothrow
  ConstIterator end() const noexcept {
    return ConstIterator(&end_);
  }

  // O(1), nothrow
  ReverseIterator rbegin() noexcept {
    return ReverseIterator(end());
  }

  // O(1), nothrow
  ConstReverseIterator rbegin() const noexcept {
    return ConstReverseIterator(end());
  }

  // O(1), nothrow
  ReverseIterator rend() noexcept {
    return ReverseIterator(begin());
  }

  // O(1), nothrow
  ConstReverseIterator rend() const noexcept {
    return ConstReverseIterator(begin());
  }

  // O(n), nothrow
  void clear() noexcept {
    while (!empty()) {
      pop_front();
    }
  }

  // O(1), strong
  template <typename U>
  Iterator insert(ConstIterator pos, U&& x) {
    Base_Node* new_node = new Node(std::forward<U>(x), pos.elem()->prev(), pos.elem());
    new_node->prev()->next_ = new_node;
    new_node->next()->prev_ = new_node;
    if (size_ != NPOS) {
      ++size_;
    }
    return Iterator(new_node);
  }

  // O(last - first), strong
  template <std::input_iterator InputIt>
  Iterator insert(ConstIterator pos, InputIt first, InputIt last) {
    if (first == last) {
      return Iterator(pos.elem());
    }
    List temp(first, last);
    Iterator begin_insert = temp.begin();
    splice(pos, temp, temp.begin(), temp.end());
    return Iterator(begin_insert);
  }

  // O(1), nothrow
  Iterator erase(ConstIterator pos) noexcept {
    pos.elem()->prev()->next_ = pos.elem()->next();
    pos.elem()->next()->prev_ = pos.elem()->prev();
    Base_Node* ans = pos.elem()->next();
    delete static_cast<Node*>(pos.elem());
    if (size_ != NPOS) {
      --size_;
    }
    return Iterator(ans);
  }

  // O(last - first), nothrow
  Iterator erase(ConstIterator first, ConstIterator last) noexcept {
    Iterator it = Iterator(first.elem());
    while (it != last) {
      it = erase(it);
    }
    return it;
  }

  // O(1), nothrow
  void splice(ConstIterator pos, List& other, ConstIterator first, ConstIterator last) noexcept {
    if (first == last) {
      return;
    }
    Base_Node* new_node = last.elem()->prev();

    make_link(first.elem()->prev(), last.elem());
    make_link(pos.elem()->prev(), first.elem());
    make_link(new_node, pos.elem());

    if (first == other.begin() && last == other.end() && other.size_ != NPOS) {
      this->size_ = other.size_;
      other.size_ = 0;
    } else {
      this->size_ = NPOS;
      other.size_ = NPOS;
    }
  }

  // O(1), nothrow
  friend void swap(List& left, List& right) noexcept {
    using std::swap;
    if (!right.empty() && !left.empty()) {
      swap(left.end_.next_, right.end_.next_);
      swap(left.end_.prev_, right.end_.prev_);
      swap(left.size_, right.size_);

      left.end_.next_->prev_ = &left.end_;
      left.end_.prev_->next_ = &left.end_;
      right.end_.next_->prev_ = &right.end_;
      right.end_.prev_->next_ = &right.end_;
    } else if (!right.empty()) {
      left.swap_links(right);
    } else if (!left.empty()) {
      swap(right, left);
    }
  }

private:
  mutable Base_Node end_;
  mutable size_t size_;

  void make_link(Base_Node* a, Base_Node* b) {
    a->next_ = b;
    b->prev_ = a;
  }

  void swap_links(List& other) noexcept {
    end_.next_ = other.end_.next_;
    end_.prev_ = other.end_.prev_;
    end_.next_->prev_ = &end_;
    end_.prev_->next_ = &end_;

    size_ = other.size_;
    other.end_.next_ = &other.end_;
    other.end_.prev_ = &other.end_;
    other.size_ = 0;
  }
};

} // namespace ct
