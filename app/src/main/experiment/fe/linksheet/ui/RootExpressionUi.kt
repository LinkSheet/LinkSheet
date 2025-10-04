package fe.linksheet.ui

import androidx.compose.runtime.Composable
import dalvik.bytecode.Opcodes
import fe.linksheet.eval.expression.OpCode


@Composable
fun RootExpressionUi(opcode: OpCode) {
    when(opcode) {
        is OpCode.Block -> BlockUI()
        else -> {}
    }
}


