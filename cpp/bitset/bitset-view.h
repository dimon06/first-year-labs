#pragma once

#include "bitset-iterator.h"
#include "bitset-reference.h"
#include "word_inf.h"

#include <algorithm>
#include <bit>
#include <cstdint>
#include <functional>

namespace ct {

template <typename T>
class BitSetView {
public:
  using Value = bool;
  using Reference = BitSetReference<T>;
  using ConstReference = BitSetReference<const T>;
  using Iterator = BitSetIterator<T>;
  using ConstIterator = BitSetIterator<const T>;
  using View = BitSetView<T>;
  using ConstView = BitSetView<const T>;

  static constexpr std::size_t NPOS = -1;
  using difference_type = std::ptrdiff_t;

  BitSetView() = default;

  BitSetView(const BitSetView& other) = default;

  BitSetView(Iterator begin, Iterator end)
      : begin_(begin)
      , end_(end) {}

  BitSetView& operator=(const BitSetView& other) = default;

  operator ConstView() const {
    return ConstView(begin_, end_);
  }

  Iterator begin() const {
    return begin_;
  }

  Iterator end() const {
    return end_;
  }

  Reference operator[](difference_type n) {
    return begin_[n];
  }

  Reference operator[](difference_type n) const {
    return begin_[n];
  }

  std::size_t size() const {
    return end_ - begin_;
  }

  bool empty() const {
    return size() == 0;
  }

  bool all_any(Word val) const {
    Iterator it = begin();
    while (it < end()) {
      word_inf inf = get_word(it);
      Word cnt1 = (((-val) << inf.offset) >> (Word_size - inf.len));
      Word cnt2 = ((inf.word << inf.offset));
      cnt2 >>= (Word_size - inf.len);
      if (cnt1 != cnt2) {
        return 1 - val;
      }
      it += inf.len;
    }
    return val;
  }

  bool all() const {
    return all_any(1);
  }

  bool any() const {
    return all_any(0);
  }

  std::size_t count() const {
    size_t ans = 0;
    Iterator it = begin();
    while (it < end()) {
      word_inf inf = get_word(it);
      ans += std::popcount((inf.word << inf.offset) >> (Word_size - inf.len));
      it += inf.len;
    }
    return ans;
  }

  template <typename Func>
  View& set_reset_flip(Func f) & {
    Iterator it = begin();
    while (it < end()) {
      word_inf inf = get_word(it);
      size_t len = put_word(it, f(inf.word));
      it += len;
    }
    return *this;
  }

  View& set() & {
    set_reset_flip([](Word x) {
      (void) x;
      return NPOS;
    });
    return *this;
  }

  View set() const& {
    View result = *this;
    result.set();
    return result;
  }

  View& reset() & {
    set_reset_flip([](Word x) {
      (void) x;
      return 0;
    });
    return *this;
  }

  View reset() const& {
    View result = *this;
    result.reset();
    return result;
  }

  View& flip() & {
    set_reset_flip([](Word x) { return NPOS ^ x; });
    return *this;
  }

  View flip() const& {
    View result = *this;
    result.flip();
    return result;
  }

  template <typename U>
  View& operator&=(const BitSetView<U>& other) & {
    and_or_xor_build_equal(*this, other, [](Word x, Word y) { return x & y; });
    return *this;
  }

  template <typename U>
  View operator&=(const BitSetView<U>& other) const& {
    View result = *this;
    result &= other;
    return result;
  }

  template <typename U>
  View& operator|=(const BitSetView<U>& other) & {
    and_or_xor_build_equal(*this, other, [](Word x, Word y) { return x | y; });
    return *this;
  }

  template <typename U>
  View operator|=(const BitSetView<U>& other) const& {
    View result = *this;
    result |= other;
    return result;
  }

  template <typename U>
  View& operator^=(const BitSetView<U>& other) & {
    and_or_xor_build_equal(*this, other, [](Word x, Word y) { return x ^ y; });
    return *this;
  }

  template <typename U>
  View operator^=(const BitSetView<U>& other) const& {
    View result = *this;
    result ^= other;
    return result;
  }

  View subview(std::size_t offset = 0, std::size_t count = NPOS) const {
    if (offset > size()) {
      return {end(), end()};
    }
    Iterator b = begin() + offset;
    Iterator e;
    if (count == NPOS || count > (size() - offset)) {
      e = end();
    } else {
      e = b + count;
    }
    return View(b, e);
  }

  void swap(BitSetView& other) noexcept {
    std::swap(begin_, other.begin_);
    std::swap(end_, other.end_);
  }

  template <typename It>
  word_inf get_word(It first) const {
    size_t offset = first.global_index & (Word_size - 1);
    std::size_t len = std::min(end().global_index - first.global_index, Word_size - offset);
    size_t array_ind = first.global_index / Word_size;
    return word_inf(first.data[array_ind], len, offset);
  }

  template <typename It>
  std::size_t put_word(It first, Word w) const {
    size_t offset = first.global_index & (Word_size - 1);
    std::size_t len = std::min(end().global_index - first.global_index, Word_size - offset);
    if (len == 0) {
      return 0;
    }

    size_t array_ind = first.global_index / Word_size;
    Word mask_all = ((len == Word_size ? NPOS : ((static_cast<Word>(1) << len) - 1)) << (Word_size - offset - len));

    first.data[array_ind] &= (~mask_all);
    first.data[array_ind] |= (w & mask_all);
    return len;
  }

private:
  Iterator begin_;
  Iterator end_;

  friend class BitSet;
};

template <typename T1, typename T2, typename Func>
void and_or_xor_build_equal(const BitSetView<T1>& first, const BitSetView<T2>& second, Func f) {
  auto it1 = first.begin();
  auto it2 = second.begin();
  word_inf tmp;
  while (it1 != first.end()) {
    word_inf inf1 = first.get_word(it1);
    word_inf inf = inf1;
    std::size_t sz = inf1.len;
    word_inf inf2 = word_inf();

    while (tmp.len + inf2.len < inf1.len) {
      if (tmp.len == 0) {
        tmp = inf2;
      }
      inf2 = second.get_word(it2);
      it2 += inf2.len;
    }
    Word val = 0;

    if (tmp.len != 0) {
      tmp.word >>= (Word_size - tmp.offset - tmp.len);
      tmp.word <<= (Word_size - inf1.offset - tmp.len);
      val = tmp.word;
      inf1.offset += tmp.len;
      inf1.len -= tmp.len;
    }
    tmp = inf2;
    tmp.offset += (inf1.len);
    tmp.len -= (inf1.len);

    inf2.word <<= inf2.offset;
    inf2.word >>= inf1.offset;

    val += inf2.word;

    if constexpr (std::is_same_v<decltype(f(inf1.word, val)), void>) {
      f(((inf1.word << inf.offset) >> (Word_size - inf.len)), ((val << inf.offset) >> (Word_size - inf.len)));
    } else {
      first.put_word(it1, f(inf1.word, val));
    }
    it1 += sz;
  }
}

template <typename T1, typename T2>
bool operator==(const BitSetView<T1>& lhs, const BitSetView<T2>& rhs) {
  if (lhs.size() != rhs.size()) {
    return false;
  }
  auto are_equal = true;
  and_or_xor_build_equal(lhs, rhs, [&are_equal](Word a, Word b) {
    if (a != b) {
      are_equal = false;
    }
  });

  return are_equal;
}

template <typename T1, typename T2>
bool operator!=(const BitSetView<T1>& lhs, const BitSetView<T2>& rhs) {
  return !(lhs == rhs);
}

template <typename T>
void swap(BitSetView<T>& lhs, BitSetView<T>& rhs) noexcept {
  lhs.swap(rhs);
}

} // namespace ct
