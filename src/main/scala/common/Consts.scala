package common

import chisel3._
import common.Instructions._

object Consts {
    val WORD_LEN = 32
    val ADDR_LEN = 32
    val MEM_SIZE = 16384
    val INST_START_ADDR = 0.U(ADDR_LEN.W)

    val ALU_SEL_LEN = 5
    val ALU_ADD     =  0.U(ALU_SEL_LEN.W)
    val ALU_SUB     =  1.U(ALU_SEL_LEN.W)
    val ALU_AND     =  2.U(ALU_SEL_LEN.W)
    val ALU_OR      =  3.U(ALU_SEL_LEN.W)
    val ALU_XOR     =  4.U(ALU_SEL_LEN.W)
    val ALU_SLL     =  5.U(ALU_SEL_LEN.W)
    val ALU_SRL     =  6.U(ALU_SEL_LEN.W)
    val ALU_SRA     =  7.U(ALU_SEL_LEN.W)
    val ALU_SLT     =  8.U(ALU_SEL_LEN.W)
    val ALU_SLTU    =  9.U(ALU_SEL_LEN.W)
    val BR_BEQ      = 10.U(ALU_SEL_LEN.W)
    val BR_BNE      = 11.U(ALU_SEL_LEN.W)
    val BR_BLT      = 12.U(ALU_SEL_LEN.W)
    val BR_BGE      = 13.U(ALU_SEL_LEN.W)
    val BR_BLTU     = 14.U(ALU_SEL_LEN.W)
    val BR_BGEU     = 15.U(ALU_SEL_LEN.W)
    val ALU_JALR    = 16.U(ALU_SEL_LEN.W)
    val ALU_X       =  0.U(ALU_SEL_LEN.W)

    val ALU_IN1_LEN = 1
    val ALU_IN1_RS1 = 0.U(ALU_IN1_LEN.W)
    val ALU_IN1_PC  = 1.U(ALU_IN1_LEN.W)
    val ALU_IN1_X   = 0.U(ALU_IN1_LEN.W)

    val ALU_IN2_LEN = 3
    val ALU_IN2_RS2 = 0.U(ALU_IN2_LEN.W)
    val ALU_IN2_IMI = 1.U(ALU_IN2_LEN.W)
    val ALU_IN2_IMS = 2.U(ALU_IN2_LEN.W)
    val ALU_IN2_IMJ = 3.U(ALU_IN2_LEN.W)
    val ALU_IN2_IMU = 4.U(ALU_IN2_LEN.W)
    val ALU_IN2_X   = 0.U(ALU_IN2_LEN.W)

    val MEM_WEN_LEN = 1
    val MEN_WEN_0   = 0.U(MEM_WEN_LEN.W)
    val MEN_WEN_1   = 1.U(MEM_WEN_LEN.W)
    val MEN_WEN_X   = 0.U(MEM_WEN_LEN.W)

    val REG_WEN_LEN = 1
    val REG_WEN_0   = 0.U(REG_WEN_LEN.W)
    val REG_WEN_1   = 1.U(REG_WEN_LEN.W)

    val WB_SEL_LEN = 3
    val WB_ALU     = 0.U(WB_SEL_LEN.W)
    val WB_MEM     = 1.U(WB_SEL_LEN.W)
    val WB_PC      = 2.U(WB_SEL_LEN.W)
    val WB_X       = 0.U(WB_SEL_LEN.W)

    // Control signals mapping
    val CONTROL_DEFAULT = List(ALU_X, ALU_IN1_X, ALU_IN2_X, MEN_WEN_X, REG_WEN_0, WB_X)
    val CONTROL_MAPPING = Array(
        LW    -> List(ALU_ADD  , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_MEM),
        SW    -> List(ALU_ADD  , ALU_IN1_RS1, ALU_IN2_IMS, MEN_WEN_1, REG_WEN_0, WB_X  ),
        ADD   -> List(ALU_ADD  , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, WB_ALU),
        ADDI  -> List(ALU_ADD  , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SUB   -> List(ALU_SUB  , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, WB_ALU),
        AND   -> List(ALU_AND  , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, WB_ALU),
        OR    -> List(ALU_OR   , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, WB_ALU),
        XOR   -> List(ALU_XOR  , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, WB_ALU),
        ANDI  -> List(ALU_AND  , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_ALU),
        ORI   -> List(ALU_OR   , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_ALU),
        XORI  -> List(ALU_XOR  , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SLL   -> List(ALU_SLL  , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SRL   -> List(ALU_SRL  , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SRA   -> List(ALU_SRA  , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SLLI  -> List(ALU_SLL  , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SRLI  -> List(ALU_SRL  , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SRAI  -> List(ALU_SRA  , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SLT   -> List(ALU_SLT  , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SLTU  -> List(ALU_SLTU , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SLTI  -> List(ALU_SLT  , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_ALU),
        SLTIU -> List(ALU_SLTU , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_ALU),
        BEQ   -> List(BR_BEQ   , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, REG_WEN_0, WB_X  ),
        BNE   -> List(BR_BNE   , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, REG_WEN_0, WB_X  ),
        BGE   -> List(BR_BGE   , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, REG_WEN_0, WB_X  ),
        BGEU  -> List(BR_BGEU  , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, REG_WEN_0, WB_X  ),
        BLT   -> List(BR_BLT   , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, REG_WEN_0, WB_X  ),
        BLTU  -> List(BR_BLTU  , ALU_IN1_RS1, ALU_IN2_RS2, MEN_WEN_0, REG_WEN_1, REG_WEN_0, WB_X  ),
        JAL   -> List(ALU_ADD  , ALU_IN1_PC , ALU_IN2_IMJ, MEN_WEN_0, REG_WEN_1, WB_PC ),
        JALR  -> List(ALU_JALR , ALU_IN1_RS1, ALU_IN2_IMI, MEN_WEN_0, REG_WEN_1, WB_PC ),
        LUI   -> List(ALU_ADD  , ALU_IN1_X  , ALU_IN2_IMU, MEN_WEN_0, REG_WEN_1, WB_ALU),
        AUIPC -> List(ALU_ADD  , ALU_IN1_PC , ALU_IN2_IMU, MEN_WEN_0, REG_WEN_1, WB_ALU),
    )
}