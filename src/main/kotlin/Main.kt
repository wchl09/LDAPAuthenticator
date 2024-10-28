import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import me.ray.ldapauthenticator.opt.Account
import ui.CodeItem
import org.jetbrains.skiko.ClipboardManager as CM

@Composable
@Preview
fun App() {
	val accounts = listOf(
		Account(name = "NAME", secret = "XXXX")
	)
	val clipboardManager = CM()
	MaterialTheme {
		LazyColumn {
			items(accounts) {
				CodeItem(it, clipboardManager)
				Box(modifier = Modifier.height(20.dp))
			}
		}
	}
}

fun main() = application {
	Window(
		onCloseRequest = ::exitApplication,
		title = "验证码",
		state = rememberWindowState(size = DpSize(220.dp, 1000.dp))
	) {
		App()
	}
}
