MAX_QWORDS:     equ             128

                section         .text

                global          _start
_start:

                ; rsp - указывает на вершину стека
                sub             rsp, 4 * MAX_QWORDS * 8         ; выделяем память для двух чисел и результата произведения
                lea             rdi, [rsp + MAX_QWORDS * 8]     ; указатель на начало записи первого числа
                mov             rcx, MAX_QWORDS                 ; в rsx лежит длина числа
                call            read_long
                mov             rdi, rsp                        ; указатель на начало записи второго числа
                call            read_long

                ; сохраняем в регистрах адреса начала множителей, результата умножения, а также длину множителей
                mov             rdi, rsp
                lea             rsi, [rsp + MAX_QWORDS * 8]
                mov             rcx, MAX_QWORDS
                lea             rbp, [rsp + 2 *MAX_QWORDS * 8]

                call            mul_long_long                   ; умножает числа

                ; сохраняем в регистрах адрес начала результата и его длину
                lea             rcx, [MAX_QWORDS * 2]
                lea             rdi, [rsp + 2 * MAX_QWORDS * 8]

                call            write_long                      ; записываем ответ

                mov             al, 0x0a
                call            write_char                      ; записываем перевод строки

                jmp             exit                            ; заканчиваем

; mul two long numbers
;    rdi -- address of summand #1 (long number)
;    rsi -- address of summand #2 (long number)
;    rcx -- length of long numbers in qwords
; result:
;    multiply is written to rbp
mul_long_long:
                push            rdi
                push            rsi
                push            rcx
                push            rbp

                ; Зануляем память перед началом умножения
                push            rdi
                push            rcx
                lea             rdi, [rbp]        ; Адрес результата
                lea             rcx, [MAX_QWORDS * 2] ; Длина результата
                call            set_zero          ; Зануляем память
                pop             rcx
                pop             rdi

                xor             r8, r8                          ; индекс разряда первого числа
                xor             r13, r13                        ; храним переполнение
.loop:
                ; перебираем индекс разряда первого числа
                xor             r9, r9                          ; индекс разряда второго числа
                lea             r15, [rbp + r8 * 8]             ; адрес разряда ответа
                mov             r14, [rdi + r8 * 8]             ; сохраняем первый множитель
                xor             r13, r13                        ; зануляем переполнение с прошлого разряда

.second_loop:
                mov             rax, r14                        ; сохраняем первый множитель
                mov             rbx, [rsi + r9 * 8]             ; сохраняем второй множитель
                mul             rbx
                add             rax, r13                        ; к меньшей части умножения прибавляем переполнение с предыдущего разряда
                adc             rdx, 0                          ; к большей прибавляем переполнение (если оно есть) от предыдущего сложения

                add             [r15], rax                      ; прибавляем к текущему адресу разряда ответа умножение двух разрядов 1 и 2 чисел
                adc             rdx, 0                          ; если разряд переполнился, то прибавляем к переполнению 1
                mov             r13, rdx                        ; сохраняем переполнение

                add             r15, 8                          ; пересчитываем адрес текущего разряда ответа
                inc             r9                              ; увеличиваем индекс, перебираемого разряда, второго числа
                cmp             r9, rcx                         ; проверяем не прошли ли все разряды во втором числе
                jl              .second_loop                    ; если разряды не кончились, то продолжаешь перебирать разряды второго числа

                ; в r15 лежит адрес текущего разряда, потому что внутренний цикл кончился, поэтому r15 + 8 находится следующий индекс результата, в котором пока лежит 0, т.к. в него ещё не разу не прибавляли(не дошли до этого)
                add             [r15], r13                      ; прибавляем в него переполнение

                inc             r8                              ; увеличиваем индекс, перебираемого разряда, первого числа
                cmp             r8, rcx                         ; проверяем не прошли ли все разряды во втором числе
                jl              .loop                           ; если разряды не кончились, то продолжаешь перебирать разряды первого числа

                pop             rbp
                pop             rcx
                pop             rsi
                pop             rdi
                ret

; adds a short number to a long number
;    rdi -- address of summand #1 (long number)
;    rax -- summand #2 (64-bit unsigned)
;    rcx -- length of long number in qwords
; result:
;    sum is written to rdi
add_long_short:
                push            rdi
                push            rcx
                push            rdx

                xor             rdx, rdx
.loop:
                add             [rdi], rax
                adc             rdx, 0
                mov             rax, rdx
                xor             rdx, rdx
                add             rdi, 8
                dec             rcx
                jnz             .loop

                pop             rdx
                pop             rcx
                pop             rdi
                ret

; multiplies a long number by a short number
;    rdi -- address of multiplier #1 (long number)
;    rbx -- multiplier #2 (64-bit unsigned)
;    rcx -- length of long number in qwords
; result:
;    product is written to rdi
mul_long_short:
                push            rax
                push            rdi
                push            rcx

                xor             rsi, rsi
.loop:
                mov             rax, [rdi]
                mul             rbx
                add             rax, rsi
                adc             rdx, 0
                mov             [rdi], rax
                add             rdi, 8
                mov             rsi, rdx
                dec             rcx
                jnz             .loop

                pop             rcx
                pop             rdi
                pop             rax
                ret

; divides a long number by a short number
;    rdi -- address of dividend (long number)
;    rbx -- divisor (64-bit unsigned)
;    rcx -- length of long number in qwords
; result:
;    quotient is written to rdi
;    remainder is written to rdx
div_long_short:
                push            rdi
                push            rax
                push            rcx

                lea             rdi, [rdi + 8 * rcx - 8]
                xor             rdx, rdx

.loop:
                mov             rax, [rdi]
                div             rbx
                mov             [rdi], rax
                sub             rdi, 8
                dec             rcx
                jnz             .loop

                pop             rcx
                pop             rax
                pop             rdi
                ret

; assigns zero to a long number
;    rdi -- argument (long number)
;    rcx -- length of long number in qwords
set_zero:
                ; сохраняем значения в регистрах
                push            rax
                push            rdi
                push            rcx

                xor             rax, rax            ; зануляем rax
                rep stosq                           ; пока rcx > 0 присваиваем по адресу rdi начение в rax

                ; востанавливаем значения в регистрах
                pop             rcx
                pop             rdi
                pop             rax
                ret                                 ; возвращаемся в место вызова

; checks if a long number is zero
;    rdi -- argument (long number)
;    rcx -- length of long number in qwords
; result:
;    ZF=1 if zero
is_zero:

                push            rax
                push            rdi
                push            rcx

                xor             rax, rax            ; зануляем rax
                rep scasq                           ; пока rcx > 0 присваиваем по адресу rdi начение в rax

                pop             rcx
                pop             rdi
                pop             rax
                ret

; reads a long number from stdin
;    rdi -- location for output (long number)
;    rcx -- length of long number in qwords
read_long:
                ; запоминаем значения в регистрах
                push            rcx
                push            rdi

                call            set_zero                    ; зануляем память для считывания числа
.loop:
                call            read_char                   ; в rax лежит считанный символ
                or              rax, rax
                js              exit                        ; если результат отрицательный, то у нас ошибка

                cmp             rax, 0x0a
                je              .done                       ; если перевод строки -> данный кончились

                cmp             rax, '0'
                jb              .invalid_char               ; < 0 то не цифра
                cmp             rax, '9'
                ja              .invalid_char               ; > 9 то не цифра

                sub             rax, '0'                    ; приводим к символу
                mov             rbx, 10
                call            mul_long_short              ; умножаем на 10
                call            add_long_short              ; прибавляем последнюю цифру
                jmp             .loop

.done:
                pop             rdi
                pop             rcx
                ret                                         ; возвращаемся

.invalid_char:
                mov             rsi, invalid_char_msg
                mov             rdx, invalid_char_msg_size
                call            print_string
                call            write_char
                mov             al, 0x0a
                call            write_char

.skip_loop:
                call            read_char
                or              rax, rax
                js              exit
                cmp             rax, 0x0a
                je              exit
                jmp             .skip_loop

; writes a long number to stdout
;    rdi -- argument (long number)
;    rcx -- length of long number in qwords
write_long:
                push            rax
                push            rcx

                mov             rax, 20
                mul             rcx
                mov             rbp, rsp
                sub             rsp, rax

                mov             rsi, rbp

.loop:
                mov             rbx, 10
                call            div_long_short
                add             rdx, '0'
                dec             rsi
                mov             [rsi], dl
                call            is_zero
                jnz             .loop

                mov             rdx, rbp
                sub             rdx, rsi
                call            print_string

                mov             rsp, rbp
                pop             rcx
                pop             rax
                ret

; reads one char from stdin
; result:
;    rax == -1 if error occurs
;    rax \in [0; 255] if OK
read_char:
                push            rcx
                push            rdi

                sub             rsp, 1                          ; смещаем вершину стека на 1, чтобы туда считать значение
                ; сигнатура sys read
                xor             rax, rax
                xor             rdi, rdi
                mov             rsi, rsp
                mov             rdx, 1
                syscall

                cmp             rax, 1                          ; если не счиаталось, то произошла ошибка
                jne             .error

                xor             rax, rax                        ; зануляем rax
                mov             al, [rsp]                       ; присваиваем в al считанное значение
                ; al - младший байт rax, поэтому теперь символ лежит в rax

                add             rsp, 1                          ; возвращаем вершину стека на прежнее место
                pop             rdi
                pop             rcx
                ret                                             ; возвращаемся
.error:
                mov             rax, -1                         ; возвращаем в rax -1, что является сигналом ошибки
                add             rsp, 1                          ; возвращаем вершину стека на прежнее место
                pop             rdi
                pop             rcx
                ret                                             ; возвращаемся

; writes one char to stdout, errors are ignored
;    al -- char
write_char:
                sub             rsp, 1
                mov             [rsp], al

                mov             rax, 1
                mov             rdi, 1
                mov             rsi, rsp
                mov             rdx, 1
                syscall
                add             rsp, 1
                ret

exit:
                mov             rax, 60
                xor             rdi, rdi
                syscall

; prints a string to stdout
;    rsi -- string
;    rdx -- size
print_string:
                push            rax

                mov             rax, 1
                mov             rdi, 1
                syscall

                pop             rax
                ret


                section         .rodata
invalid_char_msg:
                db              "Invalid character: "
invalid_char_msg_size: \
                equ             $ - invalid_char_msg
