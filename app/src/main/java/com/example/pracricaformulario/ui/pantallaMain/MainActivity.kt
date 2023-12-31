package com.example.pracricaformulario.ui.pantallaMain

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pracricaformulario.databinding.ActivityMainBinding
import com.example.pracricaformulario.domain.modelo.FichaMascota
import com.example.pracricaformulario.domain.usecases.review.AddFichaMascotaUseCase
import com.example.pracricaformulario.domain.usecases.review.DeleteFichaMascota
import com.example.pracricaformulario.domain.usecases.review.GetFichaMascotas
import com.example.pracricaformulario.domain.usecases.review.UpdateFichaMascotaUseCase
import com.example.pracricaformulario.utils.StringProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            StringProvider.instances(this),
            AddFichaMascotaUseCase(),
            GetFichaMascotas(),
            DeleteFichaMascota(),
            UpdateFichaMascotaUseCase(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)

            // Asigna un Listener al botón "Add" para crear la ficha de mascota
            buttonAdd.setOnClickListener {
                // Obtiene los valores ingresados por el usuario
                val propietario = editTextPropietario.text.toString()
                val email = editTextEmail.text.toString()
                val telefono = editTextPhone.text.toString()
                val nombreMascota = editTextNombreMascota.text.toString()

                // Obtiene el valor de especie seleccionado
                val especieSeleccionada = radioGroup.checkedRadioButtonId

                // Obtiene valor del slider
                val comportamiento = sliderComportamiento.value

                // Obtiene los valores de los CheckBox
                val esterilizado = checkBoxEsterilizado.isChecked
                val vacunado = checkBoxVacunado.isChecked

                val fichaMascota = FichaMascota(
                    propietario,
                    email,
                    telefono,
                    nombreMascota,
                    especieSeleccionada,
                    esterilizado,
                    vacunado,
                    comportamiento
                )

                viewModel.addFichaMascota(fichaMascota)
            }


            buttonSiguiente.setOnClickListener {
                viewModel.mostrarSiguienteFicha()
            }

            buttonAnterior.setOnClickListener {
                viewModel.mostrarFichaAnterior()
            }

            buttonDelete.setOnClickListener {
                viewModel.eliminarFichaActual()
            }

            buttonUpdate.setOnClickListener {
                val especieSeleccionada = radioGroup.checkedRadioButtonId

                viewModel.modificarFichaActual(
                    FichaMascota(
                        editTextPropietario.text.toString(),
                        editTextEmail.text.toString(),
                        editTextPhone.text.toString(),
                        editTextNombreMascota.text.toString(),
                        especieSeleccionada,
                        checkBoxEsterilizado.isChecked,
                        checkBoxVacunado.isChecked,
                        sliderComportamiento.value
                    )
                )
            }

            viewModel.uiState.observe(this@MainActivity) { state ->
                //Verifica si hay mensaje en el main state (que mensaje no sea igual a null)
                //En caso de que no sea null, se muestra el Toast con el contenido de mensaje
                state.mensaje?.let { mensaje ->
                    Toast.makeText(this@MainActivity, mensaje, Toast.LENGTH_SHORT).show()
                    viewModel.mensajeMostrado()
                } ?: run {
                    // Si no hay mensaje (mensaje es igual a null), muestra el nombre del propietario en el EditText
                    //sin mostrar ningun toast
                    editTextPropietario.setText(state.fichaMascota.propietario)
                    editTextEmail.setText(state.fichaMascota.email)
                    editTextPhone.setText(state.fichaMascota.telefono)
                    editTextNombreMascota.setText(state.fichaMascota.nombreMascota)
                    radioGroup.check(state.fichaMascota.especie)
                    sliderComportamiento.value = state.fichaMascota.comportamiento
                    checkBoxEsterilizado.isChecked = state.fichaMascota.esterilizado
                    checkBoxVacunado.isChecked = state.fichaMascota.vacunado
                }
            }
        }
    }
}