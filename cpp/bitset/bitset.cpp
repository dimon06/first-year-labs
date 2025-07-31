#include "bitset.h"

#include <cmath>
#include <ostream>
#include <string>

namespace ct {

BitSet::BitSet()
    : len(0)
    , bitset(nullptr) {}

BitSet::BitSet(std::size_t size)
    : len(size)
    , bitset(new Word[(len + Word_size - 1) / Word_size]()) {}

BitSet::BitSet(std::size_t size, bool value)
    : BitSet(size) {
  if (value) {
    set();
  } else {
    reset();
  }
}

BitSet::BitSet(const BitSet& other)
    : BitSet(other.size()) {
  if (size() > 0) {
    std::copy(other.bitset, other.bitset + ((len + Word_size - 1) / Word_size), bitset);
  }
}

BitSet::BitSet(std::string_view str)
    : BitSet(str.size()) {
  for (std::size_t i = 0; i < str.size(); ++i) {
    (*this)[i] = (str[i] == '1');
  }
}

BitSet::BitSet(ConstIterator first, ConstIterator last)
    : BitSet(ConstView(first, last)) {}

BitSet& BitSet::operator=(const BitSet& other) & {
  if (this != &other) {
    BitSet tmp = other;
    swap(tmp);
  }
  return *this;
}

BitSet& BitSet::operator=(std::string_view str) & {
  BitSet tmp = BitSet(str);
  this->swap(tmp);
  return *this;
}

BitSet::~BitSet() {
  delete[] bitset;
}

void BitSet::swap(BitSet& other) noexcept {
  std::swap(len, other.len);
  std::swap(bitset, other.bitset);
}

std::size_t BitSet::size() const {
  return len;
}

Word* BitSet::data() const {
  return bitset;
}

bool BitSet::empty() const {
  return (len == 0);
}

BitSet::Reference BitSet::operator[](std::size_t index) {
  return Reference(&bitset[index / Word_size], index % Word_size);
}

BitSet::ConstReference BitSet::operator[](std::size_t index) const {
  return ConstReference(&bitset[index / Word_size], index % Word_size);
}

BitSet::Iterator BitSet::begin() {
  return Iterator(bitset, 0);
}

BitSet::ConstIterator BitSet::begin() const {
  return ConstIterator(bitset, 0);
}

BitSet::Iterator BitSet::end() {
  return begin() + size();
}

BitSet::ConstIterator BitSet::end() const {
  return begin() + size();
}

BitSet::operator View() & {
  return subview();
}

BitSet::operator ConstView() const& {
  return subview();
}

BitSet& BitSet::operator&=(const ConstView& other) & {
  auto view = View(*this);
  view &= other;
  return *this;
}

BitSet& BitSet::operator|=(const ConstView& other) & {
  auto view = View(*this);
  view |= other;
  return *this;
}

BitSet& BitSet::operator^=(const ConstView& other) & {
  auto view = View(*this);
  view ^= other;
  return *this;
}

BitSet& BitSet::operator<<=(std::size_t count) & {
  BitSet bs(count + size(), false);
  bs.subview(0, size()) |= subview();
  swap(bs);
  return *this;
}

BitSet& BitSet::operator>>=(std::size_t count) & {
  BitSet bs(subview(0, (size() <= count ? 0 : size() - count)));
  if (bs.size() > 0) {
    bs.subview() |= subview(0, bs.size());
  }
  swap(bs);
  return *this;
}

BitSet& BitSet::flip() & {
  auto view = View(*this);
  view.flip();
  return *this;
}

BitSet& BitSet::set() & {
  auto view = View(*this);
  view.set();
  return *this;
}

BitSet& BitSet::reset() & {
  auto view = View(*this);
  view.reset();
  return *this;
}

bool BitSet::all() const {
  return ConstView(*this).all();
}

bool BitSet::any() const {
  return ConstView(*this).any();
}

std::size_t BitSet::count() const {
  return ConstView(*this).count();
}

inline BitSet::View view(BitSet& b) {
  return b;
}

inline BitSet::ConstView view(const BitSet& b) {
  return b;
}

BitSet::View BitSet::subview(std::size_t offset, std::size_t count) {
  if (offset > len) {
    return {end(), end()};
  }
  Iterator b = begin() + offset;
  Iterator e;
  if (count == NPOS || count > (len - offset)) {
    e = end();
  } else {
    e = b + count;
  }
  return View(b, e);
}

BitSet::ConstView BitSet::subview(std::size_t offset, std::size_t count) const {
  if (offset > len) {
    return {end(), end()};
  }
  ConstIterator b = begin() + offset;
  ConstIterator e;
  if (count == NPOS || count > (len - offset)) {
    e = end();
  } else {
    e = b + count;
  }
  return ConstView(b, e);
}

void swap(BitSet& lhs, BitSet& rhs) noexcept {
  BitSet tmp = rhs;
  rhs.swap(lhs);
  lhs.swap(tmp);
}

std::string to_string(const BitSet& bs) {
  return to_string(BitSet::ConstView(bs));
}

std::ostream& operator<<(std::ostream& out, const BitSet& bs) {
  return out << BitSet::ConstView(bs);
}

BitSet operator&(const BitSet& lhs, const BitSet& rhs) {
  BitSet result(lhs);
  result &= BitSet::ConstView(rhs);
  return result;
}

BitSet operator|(const BitSet& lhs, const BitSet& rhs) {
  BitSet result(lhs);
  result |= BitSet::ConstView(rhs);
  return result;
}

BitSet operator^(const BitSet& lhs, const BitSet& rhs) {
  BitSet result(lhs);
  result ^= BitSet::ConstView(rhs);
  return result;
}

BitSet operator~(const BitSet& bs) {
  BitSet result(bs);
  result.flip();
  return result;
}

BitSet operator<<(const BitSet& bs, std::size_t count) {
  BitSet result(bs);
  result <<= count;
  return result;
}

BitSet operator>>(const BitSet& bs, std::size_t count) {
  BitSet result(bs);
  result >>= count;
  return result;
}
} // namespace ct
