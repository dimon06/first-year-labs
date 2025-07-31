namespace ct {
struct word_inf {
  Word word;
  std::size_t len;
  std::size_t offset;

  word_inf()
      : word(0)
      , len(0)
      , offset(0) {}

  word_inf(Word w, size_t l, size_t o)
      : word(w)
      , len(l)
      , offset(o) {}
};
} // namespace ct
