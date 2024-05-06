import chisel3._

class Top extends Module {
    val io = IO(new Bundle {
        val exit = Output(Bool())  
    })

    val core = Module(new Core)
    val memory = Module(new Memory)
    val regfile = Module(new RegFile)

    // connect submodules
    core.io.imem <> memory.io.imem
    core.io.dmem <> memory.io.dmem
    core.io.regfile <> regfile.io
    io.exit := core.io.exit

    
    
}