import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import me.ray.ldapauthenticator.Authenticator.computePin
import me.ray.ldapauthenticator.opt.Account
import kotlin.time.Duration.Companion.seconds
import org.jetbrains.skiko.ClipboardManager as CM

@Composable
@Preview
fun App() {
	val account = Account(name = "", secret = "XXXX")
	var toastString by remember { mutableStateOf("可以拷贝到剪切板！") }
	var codeString by remember { mutableStateOf(account.computePin()) }
	val clipboardManager = CM()
	MaterialTheme {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Text(
				codeString,
				style = TextStyle(fontSize = 30.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold),
				letterSpacing = 10.sp,
				fontStyle = FontStyle.Italic,
				textAlign = TextAlign.Center,
				modifier = Modifier.fillMaxWidth().padding(10.dp)
			)
			timerDown {
				toastString = "可以拷贝到剪切板！"
				codeString = account.computePin()
			}
			Box(modifier = Modifier.height(20.dp))
			Button(onClick = {
				toastString = runCatching {
					clipboardManager.setText(codeString)
					"拷贝成功"
				}.onFailure {
					it.printStackTrace()
				}.getOrDefault("拷贝失败")
			}) {
				Text("拷贝验证码")
			}
			Box(Modifier.height(20.dp))
			Text(toastString)
		}

	}
}

@Composable
@Preview
inline fun timerDown(crossinline onReplay: () -> Unit) {
	//下次改变时间（下次的次数*间隔时间-当前时间）
	var time by remember { mutableStateOf(30 - (System.currentTimeMillis() / 1000).rem(30)) }
	LaunchedEffect(Unit) {
		while (true) {
			time = if (time <= 0) {
				onReplay()
				30
			} else {
				time - 1
			}
			delay(1.seconds)
		}
	}
	Text("${time}秒后改变", fontSize = 12.sp)
}

fun main() = application {
	Window(
		onCloseRequest = ::exitApplication,
		title = "LDAP验证码",
		state = rememberWindowState(size = DpSize(220.dp, 220.dp))
	) {
		App()
	}
}
