package app.linksheet.feature.scenario.ui

import androidx.compose.runtime.Composable
import app.linksheet.feature.engine.eval.expression.OpCode


@Composable
fun RootExpressionUi(opcode: OpCode) {
    when(opcode) {
        is OpCode.Block -> BlockUI()
        else -> {}
    }
}


