#include <cstddef>
#include <memory>
#include <new>
#include <utility>

namespace ct {

template <typename T>
struct DynamicBuffer {
public:
  static DynamicBuffer* create(size_t cap) {
    void* p = operator new(sizeof(DynamicBuffer) + cap * sizeof(T), std::align_val_t{alignof(T)});
    return new (p) DynamicBuffer(cap);
  }

  void destroy() noexcept {
    this->~DynamicBuffer();
    operator delete(static_cast<void*>(this), std::align_val_t{alignof(T)});
  }

  size_t size_buffer() const noexcept {
    return size_;
  }

  size_t cnt_reference() const noexcept {
    return cnt_reference_;
  }

  T* data() noexcept {
    return data_;
  }

  const T* data() const noexcept {
    return data_;
  }

  void inc_ref() noexcept {
    ++cnt_reference_;
  }

  bool dec_ref() noexcept {
    return (--cnt_reference_) == 0;
  }

private:
  explicit DynamicBuffer(size_t cap) noexcept
      : size_(cap)
      , cnt_reference_(1) {}

  DynamicBuffer(const DynamicBuffer&) = delete;
  DynamicBuffer& operator=(const DynamicBuffer&) = delete;

  size_t size_;
  size_t cnt_reference_;
  T data_[0];
};

} // namespace ct
