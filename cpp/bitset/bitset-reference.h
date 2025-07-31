#pragma once

#include <cstddef>
#include <cstdint>
#include <limits>
#include <type_traits>

namespace ct {

using Word = uint64_t;
static constexpr Word Word_size = std::numeric_limits<Word>::digits;

class BitSet;

template <typename T>
class BitSetReference {
public:
  BitSetReference(Word* block, std::size_t index)
      : block(block)
      , index(1ULL << (Word_size - index - 1)) {}

  template <typename U>
  BitSetReference(const BitSetReference<U>& other)
    requires (std::is_same_v<T, const U>)
      : block(other.block)
      , index(other.index) {}

  BitSetReference& operator=(bool value) {
    if (value) {
      *block |= index;
    } else {
      *block &= ~index;
    }
    return *this;
  }

  operator bool() const {
    return (*block & index) != 0;
  }

  void flip() {
    *block ^= index;
  }

private:
  Word* block;
  std::size_t index;

  template <typename U>
  friend class BitSetReference;
};

} // namespace ct
