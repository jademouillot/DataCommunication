package fr.isen.mouillot.datacommunication

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.mouillot.datacommunication.ui.theme.DataCommunicationTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : ComponentActivity() {

    private lateinit var mqttClient: MqttAndroidClient
    companion object {
        const val TAG = "AndroidMqttClient"
    }

    var button_button_cliqued = false
    var button_valider_cliqued = false
    var button_temp_cliqued = false
    var button_connected_cliqued = false
    var button_deconnected_cliqued = false

    var verif = false
    var verif_temp = false

    var firstChar: Char? = null
    var thirdChar: Char? = null

    private var receivedMessages_button: List<String> by mutableStateOf(emptyList())
    private var receivedMessages_temp: List<String> by mutableStateOf(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DataCommunicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val context = LocalContext.current

                    var text by remember { mutableStateOf(TextFieldValue()) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        //verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Titre de la page
                        Text(
                            text = "Data Communication Project",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Start,
                        )

                        Spacer(modifier = Modifier.height(50.dp))

                        Button(
                            onClick = {button_connected_cliqued = true},
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = "Se connecter")
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Bouton centré
                        Button(
                            onClick = {
                                verif = true
                                button_button_cliqued = !button_button_cliqued
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                text = if (button_button_cliqued) "Ne plus recevoir les données du bouton" else "Recevoir les données du bouton"
                            )
                        }
                        // Zone de texte pour afficher les données du bouton reçues
                        Text(
                            text = if (button_button_cliqued) {
                                // Si le bouton a été cliqué, afficher les valeurs reçues
                                if (receivedMessages_button.isEmpty()) "Pas de valeurs de bouton reçues"
                                else receivedMessages_button.joinToString(separator = "\n")
                            } else {
                                // Si le bouton n'a pas été cliqué, afficher un message par défaut
                                "Pas de valeurs de bouton reçues"
                            },
                            modifier = Modifier.padding(top = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // Champ de texte
                        Text(
                            text = "Veuillez saisir la LED que vous voulez modifier (de 1 à 3), puis si vous voulez l'allumer ou l'éteindre (1 ou 0)",
                            modifier = Modifier.padding(top = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        // Champ de texte
                        TextField(
                            value = text,
                            onValueChange = {
                                text = it
                                firstChar = it.text.firstOrNull()
                                thirdChar = it.text.getOrNull(2)
                                println("Premier caractère: $firstChar")
                                println("Troisième caractère: $thirdChar")
                            },
                            label = { Text("Entrez la LED ici") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )

                        // Bouton Valider
                        Button(
                            onClick = { button_valider_cliqued = true },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = "Valider")
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Autre bouton en dessous
                        // Bouton centré
                        Button(
                            onClick = {
                                verif_temp = true
                                button_temp_cliqued = !button_temp_cliqued
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                text = if (button_temp_cliqued) "Ne plus recevoir les données du capteur de température" else "Recevoir les données du capteur de température"
                            )
                        }
                        // Zone de texte pour afficher les données du bouton reçues
                        Text(
                            text = if (button_temp_cliqued) {
                                // Si le bouton a été cliqué, afficher les valeurs reçues
                                if (receivedMessages_temp.isEmpty()) "Pas de valeurs de température reçues"
                                else receivedMessages_temp.joinToString(separator = "\n")
                            } else {
                                // Si le bouton n'a pas été cliqué, afficher un message par défaut
                                "Pas de valeurs de température reçues"
                            },
                            modifier = Modifier.padding(top = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = { button_deconnected_cliqued = true },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = "Se déconnecter")
                        }
                    }
                    if(button_connected_cliqued){
                        connect(context)
                    }
                    if(button_deconnected_cliqued){
                        disconnect()
                    }
                    if(button_button_cliqued){
                        subscribe("isen15/button", 1)
                    }
                    if(verif && !button_button_cliqued){
                        unsubscribe("isen15/button")
                    }
                    //unsuscribe ??
                    if(button_valider_cliqued){
                        val ledMessage = LedMessage(id = firstChar, state = thirdChar)
                        // Sérialisation de l'objet en JSON
                        val jsonString = Json.encodeToString(ledMessage)
                        publish("isen15/led", jsonString, 1, false)
                    }
                    if(button_temp_cliqued){
                        val tempMessage = TempCapt(request = 1)
                        // Sérialisation de l'objet en JSON
                        val jsonString = Json.encodeToString(tempMessage)
                        publish("isen15/getTemp", jsonString, 1, false)
                        subscribe("isen15/temp", 1)
                    }
                    if(verif_temp && !button_temp_cliqued){
                        unsubscribe("isen15/temp")
                    }
                    //unsuscribe ??
                }
            }
        }


    }
    fun connect(context: Context) {
        val serverURI = "broker.hivemq.com:1883"
        mqttClient = MqttAndroidClient(context, serverURI, "kotlin_client")
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG, "Receive message: ${message.toString()} from topic: $topic")
                when(topic) {
                    "isen15/button" -> {
                        // Traitement pour le topic "isen15/button"
                        // Par exemple, mettre à jour une chaîne de caractères spécifique pour ce topic
                        receivedMessages_button = receivedMessages_button + message.toString()
                    }

                    "isen15/temp" -> {
                        // Traitement pour un autre topic
                        // Par exemple, mettre à jour une autre chaîne de caractères pour ce topic
                        receivedMessages_temp = receivedMessages_temp + message.toString()
                    }
                }
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "Connection lost ${cause.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }
        })
        val options = MqttConnectOptions()
        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connection success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }
    fun subscribe(topic: String, qos: Int = 1) {
        try {
            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to subscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
    fun unsubscribe(topic: String) {
        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Unsubscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to unsubscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "$msg published to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to publish $msg to $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
    fun disconnect() {
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Disconnected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to disconnect")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}
