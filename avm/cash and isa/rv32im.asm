lui gp, 1
    addi gp, sp, 9

    sh gp, 0, a0
    sb gp, 1, a1
    sw gp, 2, a2
    sh gp, 16, a3
    sb gp, 32, a4
    sw gp, 512, a5
    sh gp, 1024, a6
    sw gp, 1536, a7

    fence iorw, iorw
    fence i, i
    fence w, w

    ori s10, s11, 12
    srl s2, s3, s4
    xor s7, s8, s9

    addi sp, s1, 1
    addi s1, sp, 0

    beq gp, sp, -1024
    beq t5, t6, -24
