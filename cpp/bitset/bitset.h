#pragma once

#include "bitset-iterator.h"
#include "bitset-reference.h"
#include "bitset-view.h"

#include <algorithm>
#include <iterator>
#include <ostream>
#include <string>
#include <string_view>

namespace ct {

class BitSet {
public:
  using Value = bool;
  using Reference = BitSetReference<Value>;
  using ConstReference = BitSetReference<const Value>;
  using Iterator = BitSetIterator<Value>;
  using ConstIterator = BitSetIterator<const Value>;
  using View = BitSetView<Value>;
  using ConstView = BitSetView<const Value>;
  using Word = ct::Word;

  static constexpr std::size_t NPOS = -1;

  BitSet();
  BitSet(std::size_t size);
  BitSet(std::size_t size, bool value);
  BitSet(const BitSet& other);
  explicit BitSet(std::string_view str);

  template <typename T>
  explicit BitSet(const BitSetView<T>& view)
      : BitSet(view.size()) {
    auto sv = this->subview();
    and_or_xor_build_equal(sv, view, [](Word x, Word y) {
      (void) x;
      return y;
    });
  }

  explicit BitSet(ConstIterator first, ConstIterator last);

  BitSet& operator=(const BitSet& other) &;
  BitSet& operator=(std::string_view str) &;

  template <typename T>
  BitSet& operator=(const BitSetView<T>& view) & {
    BitSet tmp(view);
    swap(tmp);
    return *this;
  }

  ~BitSet();

  void swap(BitSet& other) noexcept;

  std::size_t size() const;
  Word* data() const;
  bool empty() const;

  Reference operator[](std::size_t index);
  ConstReference operator[](std::size_t index) const;

  Iterator begin();
  ConstIterator begin() const;

  Iterator end();
  ConstIterator end() const;

  BitSet& operator&=(const ConstView& other) &;
  BitSet& operator|=(const ConstView& other) &;
  BitSet& operator^=(const ConstView& other) &;
  BitSet& operator<<=(std::size_t count) &;
  BitSet& operator>>=(std::size_t count) &;
  BitSet& flip() &;

  BitSet& set() &;
  BitSet& reset() &;

  bool all() const;
  bool any() const;
  std::size_t count() const;

  operator ConstView() const&;
  operator View() &;

  operator ConstView() const&& {
    return subview();
  }

  operator View() && {
    return subview();
  }

  View subview(std::size_t offset = 0, std::size_t count = NPOS);

  ConstView subview(std::size_t offset = 0, std::size_t count = NPOS) const;

private:
  std::size_t len = 0;
  Word* bitset = nullptr;
};

BitSet operator&(const BitSet& lhs, const BitSet& rhs);

BitSet operator|(const BitSet& lhs, const BitSet& rhs);

BitSet operator^(const BitSet& lhs, const BitSet& rhs);

BitSet operator~(const BitSet& bs);

BitSet operator<<(const BitSet& bs, std::size_t count);

BitSet operator>>(const BitSet& bs, std::size_t count);

template <typename T>
inline BitSetView<T> operator|=(BitSetView<T> lhs, const BitSet& rhs) {
  lhs |= BitSet::ConstView(rhs);
  return lhs;
}

template <typename T>
inline BitSetView<T> operator&=(BitSetView<T> lhs, const BitSet& rhs) {
  lhs &= BitSet::ConstView(rhs);
  return lhs;
}

template <typename T>
inline BitSetView<T> operator^=(BitSetView<T> lhs, const BitSet& rhs) {
  lhs ^= BitSet::ConstView(rhs);
  return lhs;
}

inline bool operator==(const BitSet& left, const BitSet& right) {
  return BitSet::ConstView(left) == BitSet::ConstView(right);
}

inline bool operator!=(const BitSet& left, const BitSet& right) {
  return BitSet::ConstView(left) != BitSet::ConstView(right);
}

template <typename T>
inline bool operator==(const BitSetView<T>& view, const BitSet& bs) {
  return view == BitSet::ConstView(bs);
}

template <typename T>
inline bool operator!=(const BitSetView<T>& view, const BitSet& bs) {
  return !(view == bs);
}

template <typename T>
inline bool operator==(const BitSet& bs, const BitSetView<T>& view) {
  return BitSet::ConstView(bs) == view;
}

template <typename T>
inline bool operator!=(const BitSet& bs, const BitSetView<T>& view) {
  return !(bs == view);
}

template <typename T>
inline std::string to_string(const BitSetView<T>& view) {
  std::string s;
  s.reserve(view.size());
  for (bool b : view) {
    s.push_back(b ? '1' : '0');
  }
  return s;
}

template <typename T>
std::ostream& operator<<(std::ostream& out, const BitSetView<T>& view) {
  for (std::size_t i = 0; i < view.size(); ++i) {
    out << (view[i] ? '1' : '0');
  }
  return out;
}

void swap(BitSet& lhs, BitSet& rhs) noexcept;

std::string to_string(const BitSet& bs);

std::ostream& operator<<(std::ostream& out, const BitSet& bs);

template <typename T1, typename T2>
inline BitSet operator&(const BitSetView<T1>& lhs, const BitSetView<T2>& rhs) {
  BitSet result(lhs.begin(), lhs.end());
  return result &= rhs;
}

template <typename T1, typename T2>
inline BitSet operator|(const BitSetView<T1>& lhs, const BitSetView<T2>& rhs) {
  BitSet result(lhs.begin(), lhs.end());
  return result |= rhs;
}

template <typename T1, typename T2>
inline BitSet operator^(const BitSetView<T1>& lhs, const BitSetView<T2>& rhs) {
  BitSet result(lhs.begin(), lhs.end());
  return result ^= rhs;
}

template <typename T>
inline BitSet operator~(const BitSetView<T>& v) {
  BitSet result(v.begin(), v.end());
  result.flip();
  return result;
}

template <typename T>
inline BitSet operator<<(const BitSetView<T>& v, std::size_t s) {
  BitSet result(v.begin(), v.end());
  result <<= s;
  return result;
}

template <typename T>
inline BitSet operator>>(const BitSetView<T>& v, std::size_t s) {
  BitSet result(v.begin(), v.end());
  result >>= s;
  return result;
}

template <typename T>
inline BitSet operator&(const BitSet& lhs, const BitSetView<T>& rhs) {
  BitSet tmp(lhs);
  tmp &= rhs;
  return tmp;
}

template <typename T>
inline BitSet operator&(const BitSetView<T>& lhs, const BitSet& rhs) {
  BitSet tmp(rhs);
  tmp &= lhs;
  return tmp;
}

template <typename T>
inline BitSet operator|(const BitSet& lhs, const BitSetView<T>& rhs) {
  BitSet tmp(lhs);
  tmp |= rhs;
  return tmp;
}

template <typename T>
inline BitSet operator|(const BitSetView<T>& lhs, const BitSet& rhs) {
  BitSet tmp(rhs);
  tmp |= lhs;
  return tmp;
}

template <typename T>
inline BitSet operator^(const BitSet& lhs, const BitSetView<T>& rhs) {
  BitSet tmp(lhs);
  tmp ^= rhs;
  return tmp;
}

template <typename T>
inline BitSet operator^(const BitSetView<T>& lhs, const BitSet& rhs) {
  BitSet tmp(rhs);
  tmp ^= lhs;
  return tmp;
}

} // namespace ct
