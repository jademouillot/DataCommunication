package fr.isen.mouillot.datacommunication


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.mouillot.datacommunication.ui.theme.DataCommunicationTheme
import java.util.Timer
import kotlin.concurrent.timerTask

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    private val SPLASH_TIME_OUT: Long = 5000 // 5 secondes

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Créer une ImageView pour afficher le logo
        //val imageView = ImageView(this)
        //imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.logo_projet))

        setContent {
            DataCommunicationTheme {

                // Cette coroutine attend le temps défini avant de démarrer l'activité suivante
                SplashScreenContent()
            }
        }
        Timer().schedule(timerTask {
            navigateToNextScreen()
        }, SPLASH_TIME_OUT)


    }

    // Cette fonction est appelée lorsque le délai est écoulé, elle démarre l'activité suivante
    private fun navigateToNextScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Ajouter un padding si nécessaire
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .size(350.dp) // Taille de la colonne, ajustez selon vos besoins
                .wrapContentHeight(align = Alignment.CenterVertically) // Centrer verticalement la colonne dans la Box
        ) {
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(30.dp)) // Espace vertical de 8dp
            Text(
                text = "BOTANICARE",
                color = Color.Green, // Couleur du texte en vert
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    //fontFamily = FontFamily(Font(R.font.montserrat_regular)), // Définir la police Montserrat
                    fontSize = 20.sp // Taille du texte en sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

