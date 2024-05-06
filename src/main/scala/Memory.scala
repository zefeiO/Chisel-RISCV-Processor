import chisel3._
import common.Consts._
import chisel3.util.Cat
import chisel3.util.experimental.loadMemoryFromFile

// Imem interface
class ImemPortIO extends Bundle {
    val addr = Input(UInt(ADDR_LEN.W))
    val inst = Output(UInt(WORD_LEN.W))
}

// Dmem interface
class DmemPortIO extends Bundle {
    val wen = Input(Bool())
    val addr = Input(UInt(ADDR_LEN.W))
    val data_in = Input(UInt(WORD_LEN.W))
    val data_out = Output(UInt(WORD_LEN.W))
}

class Memory extends Module {
    val io = IO(new Bundle {
        val imem = new ImemPortIO
        val dmem = new DmemPortIO
    })

    // byte-addressable memory
    val mem = Mem(MEM_SIZE, UInt(8.W))

    loadMemoryFromFile(mem, "src/hex/instructions.hex")

    // Imem
    io.imem.inst := Cat(
        mem(io.imem.addr + 3.U(ADDR_LEN.W)),
        mem(io.imem.addr + 2.U(ADDR_LEN.W)),
        mem(io.imem.addr + 1.U(ADDR_LEN.W)),
        mem(io.imem.addr + 0.U(ADDR_LEN.W)),
    )

    // Dmem
    io.dmem.data_out := Cat(
        mem(io.dmem.addr + 3.U(WORD_LEN.W)),
        mem(io.dmem.addr + 2.U(WORD_LEN.W)), 
        mem(io.dmem.addr + 1.U(WORD_LEN.W)),
        mem(io.dmem.addr)
    )
    when(io.dmem.wen){
        mem(io.dmem.addr)       := io.dmem.data_in( 7, 0)
        mem(io.dmem.addr + 1.U) := io.dmem.data_in(15, 8)
        mem(io.dmem.addr + 2.U) := io.dmem.data_in(23,16)
        mem(io.dmem.addr + 3.U) := io.dmem.data_in(31,24)
    }
}