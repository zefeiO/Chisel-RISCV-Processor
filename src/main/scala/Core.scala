import chisel3._
import chisel3.util._
import common.Consts._
import common.Instructions._

class Core extends Module {
    val io = IO(new Bundle {
        val imem = Flipped(new ImemPortIO)
        val dmem = Flipped(new DmemPortIO)
        val regfile = Flipped(new RegFilePortIO)
        val exit = Output(Bool())
    })

    // Signal Declarations
    val br_taken = Wire(Bool())
    val br_target = Wire(UInt(ADDR_LEN.W))
    val jump = Wire(Bool())
    val alu_out = Wire(UInt(WORD_LEN.W))

    //**********************************
    // Fetch Stage
    val pc_r = RegInit(INST_START_ADDR)
    pc_r := MuxCase(pc_r + 4.U(ADDR_LEN.W), Seq(
        br_taken -> br_target,
        jump -> alu_out
    ))
    pc_r := Mux(br_taken, br_target, pc_r + 4.U(ADDR_LEN.W))

    io.imem.addr := pc_r
    val inst = io.imem.inst

    //**********************************
    // Decode Stage
    val rs1_addr = inst(19, 15)
    val rs2_addr = inst(24, 20)
    val rd_addr = inst(11, 7)
    jump := inst === JAL || inst === JALR

    // read from regfile
    io.regfile.rs1_addr := rs1_addr
    io.regfile.rs2_addr := rs2_addr
    io.regfile.rd_addr := rd_addr
    val rs1 = io.regfile.rs1_data
    val rs2 = io.regfile.rs2_data

    // Immediate generation
    val imm_i = inst(31, 20)
    val imm_i_out = Cat(Fill(20, imm_i(11)), imm_i)
    val imm_s = Cat(inst(31, 25), inst(11, 7))
    val imm_s_out = Cat(Fill(20, imm_s(11)), imm_s)
    val imm_b = Cat(inst(31), inst(7), inst(30, 25), inst(11, 8))
    val imm_b_out = Cat(Fill(19, imm_b(11)), imm_b, 0.U(1.W))
    val imm_j = Cat(inst(31), inst(19, 12), inst(20), inst(30, 21))
    val imm_j_out = Cat(Fill(11, imm_j(19)), imm_j, 0.U(1.W))
    val imm_u = inst(31,12)
    val imm_u_out = Cat(imm_u, Fill(12, 0.U))

    val alu_sel :: alu_in1_sel :: alu_in2_sel :: dmem_wen :: reg_wen :: wb_sel :: Nil = ListLookup(inst, CONTROL_DEFAULT, CONTROL_MAPPING)
    
    //**********************************
    // Execute Stage

    val alu_in1 = MuxCase(rs1, Seq(
        (alu_in1_sel === ALU_IN1_RS1) -> rs1,
        (alu_in1_sel === ALU_IN1_PC) -> pc_r
    ))

    val alu_in2 = MuxCase(rs2, Seq(
        (alu_in2_sel === ALU_IN2_RS2) -> rs2,
        (alu_in2_sel === ALU_IN2_IMI) -> imm_i_out,
        (alu_in2_sel === ALU_IN2_IMS) -> imm_s_out,
        (alu_in2_sel === ALU_IN2_IMJ) -> imm_j_out,
        (alu_in2_sel === ALU_IN2_IMU) -> imm_u_out
    ))

    alu_out := MuxCase(0.U(WORD_LEN.W), Seq(
        (alu_sel === ALU_ADD)  -> (alu_in1 + alu_in2),
        (alu_sel === ALU_SUB)  -> (alu_in1 - alu_in2),
        (alu_sel === ALU_AND)  -> (alu_in1 & alu_in2),
        (alu_sel === ALU_OR)   -> (alu_in1 | alu_in2),
        (alu_sel === ALU_XOR)  -> (alu_in1 ^ alu_in2),
        (alu_sel === ALU_SLL)  -> (alu_in1 << alu_in2(4, 0))(31, 0),
        (alu_sel === ALU_SRL)  -> (alu_in1 >> alu_in2(4, 0)),
        (alu_sel === ALU_SRA)  -> (alu_in1.asSInt >> alu_in2(4, 0)).asUInt,
        (alu_sel === ALU_SLT)  -> (alu_in1.asSInt < alu_in2.asSInt).asUInt,
        (alu_sel === ALU_SLTU) -> (alu_in1 < alu_in2).asUInt,
        (alu_sel === ALU_JALR) -> ((alu_in1 + alu_in2) & ~1.U(ADDR_LEN.W))
    ))

    br_taken := MuxCase(false.B, Seq(
        (alu_sel === BR_BEQ)  ->  (alu_in1 === alu_in2),
        (alu_sel === BR_BNE)  -> !(alu_in1 === alu_in2),
        (alu_sel === BR_BLT)  ->  (alu_in1.asSInt < alu_in2.asSInt),
        (alu_sel === BR_BGE)  -> !(alu_in1.asSInt < alu_in2.asSInt),
        (alu_sel === BR_BLTU) ->  (alu_in1 < alu_in2),
        (alu_sel === BR_BGEU) -> !(alu_in1 < alu_in2)
    ))
    br_target := pc_r + imm_b_out

    //**********************************
    // Memory Stage
    io.dmem.wen := dmem_wen
    io.dmem.addr := alu_out
    io.dmem.data_in := rs2

    //**********************************
    // Writeback (WB) Stage
    val wb_data = io.dmem.data_out
    io.regfile.reg_wen := reg_wen
    io.regfile.rd_addr := rd_addr
    io.regfile.data_d := wb_data
    

    // Debug
    io.exit := (pc_r === 0x44.U(WORD_LEN.W))
    printf(p"io.pc          : 0x${Hexadecimal(pc_r)}\n")
    printf(p"inst           : 0x${Hexadecimal(inst)}\n")
    printf(p"rs1_addr       : $rs1_addr\n")
    printf(p"rs2_addr       : $rs2_addr\n")
    printf(p"rd_addr        : $rd_addr\n")
    printf(p"rs1_data       : 0x${Hexadecimal(rs1)}\n")
    printf(p"rs2_data       : 0x${Hexadecimal(rs2)}\n")
    printf(p"wb_data        : 0x${Hexadecimal(wb_data)}\n")
    printf(p"dmem.addr      : ${io.dmem.addr}\n")
    printf(p"dmem.data_out  : ${io.dmem.data_out}\n")
    printf("---------\n")
}