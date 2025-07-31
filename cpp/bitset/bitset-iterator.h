#pragma once

#include "bitset-reference.h"

#include <cstdint>
#include <iterator>

namespace ct {

class BitSet;

template <typename T>
class BitSetIterator {
public:
  using value_type = bool;
  using reference = BitSetReference<T>;
  using pointer = void;
  using difference_type = std::ptrdiff_t;
  using iterator_category = std::random_access_iterator_tag;

  BitSetIterator() = default;

  BitSetIterator(Word* data, std::size_t global_index)
      : data(data)
      , global_index(global_index) {}

  operator BitSetIterator<const T>() const {
    return BitSetIterator<const T>(data, global_index);
  }

  ~BitSetIterator() = default;

  reference operator*() {
    return reference(&data[global_index / Word_size], global_index % Word_size);
  }

  reference operator*() const {
    return reference(&data[global_index / Word_size], global_index % Word_size);
  }

  reference operator[](difference_type n) {
    return *(*this + n);
  }

  reference operator[](difference_type n) const {
    return *(*this + n);
  }

  BitSetIterator& operator++() {
    global_index++;
    return *this;
  }

  BitSetIterator operator++(int) {
    BitSetIterator result = *this;
    ++(*this);
    return result;
  }

  BitSetIterator& operator--() {
    global_index--;
    return *this;
  }

  BitSetIterator operator--(int) {
    BitSetIterator result = *this;
    --(*this);
    return result;
  }

  BitSetIterator& operator+=(difference_type n) {
    global_index += n;
    return *this;
  }

  BitSetIterator operator+(difference_type n) const {
    BitSetIterator tmp = *this;
    tmp += n;
    return tmp;
  }

  friend BitSetIterator operator+(difference_type n, const BitSetIterator& it) {
    return it + n;
  }

  BitSetIterator& operator-=(difference_type n) {
    global_index -= n;
    return *this;
  }

  BitSetIterator operator-(difference_type n) const {
    BitSetIterator tmp = *this;
    tmp -= n;
    return tmp;
  }

private:
  Word* data;
  std::size_t global_index;

  template <typename T1, typename T2>
  friend BitSetIterator<T1>::difference_type operator-(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs);

  template <typename T1, typename T2>
  friend bool operator==(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs);

  template <typename T1, typename T2>
  friend bool operator!=(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs);

  template <typename T1, typename T2>
  friend bool operator<(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs);

  template <typename T1, typename T2>
  friend bool operator<=(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs);

  template <typename T1, typename T2>
  friend bool operator>(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs);

  template <typename T1, typename T2>
  friend bool operator>=(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs);

  friend class BitSet;
  template <typename>
  friend class BitSetView;
  template <typename>
  friend class BitSetIterator;
};

template <typename T1, typename T2>
BitSetIterator<T1>::difference_type operator-(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs) {
  return lhs.global_index - rhs.global_index;
}

template <typename T1, typename T2>
bool operator==(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs) {
  return lhs.global_index == rhs.global_index;
}

template <typename T1, typename T2>
bool operator!=(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs) {
  return !(lhs == rhs);
}

template <typename T1, typename T2>
bool operator<(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs) {
  return lhs.global_index < rhs.global_index;
}

template <typename T1, typename T2>
bool operator<=(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs) {
  return lhs.global_index <= rhs.global_index;
}

template <typename T1, typename T2>
bool operator>(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs) {
  return !(lhs <= rhs);
}

template <typename T1, typename T2>
bool operator>=(const BitSetIterator<T1>& lhs, const BitSetIterator<T2>& rhs) {
  return !(lhs < rhs);
}
} // namespace ct
