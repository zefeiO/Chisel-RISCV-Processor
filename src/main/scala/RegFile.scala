import chisel3._
import chisel3.util._
import common.Consts._

class RegFilePortIO extends Bundle {
    val rs1_addr = Input(UInt(5.W))
    val rs2_addr = Input(UInt(5.W))
    val rd_addr = Input(UInt(5.W))
    val data_d = Input(UInt(WORD_LEN.W))
    val reg_wen = Input(Bool())

    val rs1_data = Output(UInt(WORD_LEN.W))
    val rs2_data = Output(UInt(WORD_LEN.W))
}

class RegFile extends Module {
    val io = IO(new RegFilePortIO)

    val registers = Mem(32, UInt(5.W))

    // always read
    io.rs1_data := Mux(io.rs1_addr =/= 0.U(5.W), registers(io.rs1_addr), 0.U(WORD_LEN.W))
    io.rs2_data := Mux(io.rs2_addr =/= 0.U(5.W), registers(io.rs2_addr), 0.U(WORD_LEN.W))
    
    // write when enabled
    when(io.reg_wen && (io.rd_addr =/= 0.U(5.W))){
        registers(io.rd_addr) := io.data_d
    }
}