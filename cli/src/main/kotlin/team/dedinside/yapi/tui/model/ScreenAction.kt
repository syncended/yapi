package team.dedinside.yapi.tui.model

import team.dedinside.yapi.tui.core.Screen

sealed class ScreenAction {
    /** Stay on the current screen and re-render. */
    data object Stay : ScreenAction()

    /** Push a new screen on top of this one. */
    data class Push(val screen: Screen) : ScreenAction()

    /** Replace the current screen with a new one (pop + push). */
    data class Replace(val screen: Screen) : ScreenAction()

    /** Pop the current screen; the controller exits the app if the stack becomes empty. */
    data object Pop : ScreenAction()

    /** Exit the TUI loop entirely. */
    data object Exit : ScreenAction()
}
