package com.duoc.dsy1105.viewmodel

import android.app.Application
import com.duoc.dsy1105.model.Usuario
import com.duoc.dsy1105.repository.AuthRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun crearViewModel(
        repository: AuthRepository = mockk(relaxed = true),
        emailValido: (String) -> Boolean = { true }
    ): LoginViewModel {

        coEvery {
            repository.asegurarUsuariosDemo()
        } returns Unit

        return LoginViewModel(
            app = mockk<Application>(relaxed = true),
            authRepository = repository,
            emailValido = emailValido
        )
    }

    "login vacio marca email y password como obligatorios" {
        val viewModel = crearViewModel()

        viewModel.login()

        val errores = viewModel.loginEstado.value.errores

        errores.email shouldBe "El email es obligatorio"
        errores.password shouldBe "La contraseña es obligatoria"
        viewModel.usuario.value.shouldBeNull()
    }

    "login rechaza formato de email invalido" {
        val viewModel = crearViewModel(
            emailValido = { false }
        )

        viewModel.onEmailChange("correo-malo")
        viewModel.onPasswordChange("12345")
        viewModel.login()

        viewModel.loginEstado.value.errores.email shouldBe
                "Ingresa un email válido"
    }

    "editar email y password limpia sus errores" {
        val viewModel = crearViewModel()

        viewModel.login()

        viewModel.loginEstado.value.errores.email.shouldNotBeNull()
        viewModel.loginEstado.value.errores.password.shouldNotBeNull()

        viewModel.onEmailChange("ejecutivo@agencia.cl")
        viewModel.onPasswordChange("12345")

        viewModel.loginEstado.value.errores.email.shouldBeNull()
        viewModel.loginEstado.value.errores.password.shouldBeNull()
    }

    "credenciales incorrectas muestran error" {
        val repository = mockk<AuthRepository>(relaxed = true)

        coEvery {
            repository.asegurarUsuariosDemo()
        } returns Unit

        coEvery {
            repository.login(
                "incorrecto@agencia.cl",
                "12345"
            )
        } returns null

        val viewModel = crearViewModel(repository)

        viewModel.onEmailChange("incorrecto@agencia.cl")
        viewModel.onPasswordChange("12345")

        viewModel.login()

        viewModel.usuario.value.shouldBeNull()

        viewModel.loginEstado.value.error shouldBe
                "Email o contraseña incorrectos"
    }

    "usuario desactivado no puede iniciar sesion" {
        val repository = mockk<AuthRepository>(relaxed = true)

        val usuario = mockk<Usuario>()
        io.mockk.every { usuario.estado } returns false

        coEvery {
            repository.login(
                "usuario@agencia.cl",
                "12345"
            )
        } returns usuario

        val viewModel = crearViewModel(repository)

        viewModel.onEmailChange("usuario@agencia.cl")
        viewModel.onPasswordChange("12345")

        viewModel.login()

        viewModel.usuario.value.shouldBeNull()

        viewModel.loginEstado.value.error shouldBe
                "Tu cuenta está desactivada. Contacta a un supervisor."
    }

    "usuario activo inicia sesion correctamente" {
        val repository = mockk<AuthRepository>(relaxed = true)

        val usuario = mockk<Usuario>()
        io.mockk.every { usuario.estado } returns true

        coEvery {
            repository.login(
                "ejecutivo@agencia.cl",
                "12345"
            )
        } returns usuario

        val viewModel = crearViewModel(repository)

        viewModel.onEmailChange("ejecutivo@agencia.cl")
        viewModel.onPasswordChange("12345")

        viewModel.login()

        viewModel.usuario.value shouldBe usuario
        viewModel.loginEstado.value.error.shouldBeNull()
    }

    "cerrar sesion limpia usuario login y registro" {
        val repository = mockk<AuthRepository>(relaxed = true)

        val usuario = mockk<Usuario>()
        io.mockk.every { usuario.estado } returns true

        coEvery {
            repository.login(any(), any())
        } returns usuario

        val viewModel = crearViewModel(repository)

        viewModel.onEmailChange("ejecutivo@agencia.cl")
        viewModel.onPasswordChange("12345")
        viewModel.login()

        viewModel.onRegistroNombreChange("Prueba")
        viewModel.onRegistroEmailChange("prueba@test.cl")

        viewModel.cerrarSesion()

        viewModel.usuario.value.shouldBeNull()
        viewModel.loginEstado.value.email shouldBe ""
        viewModel.loginEstado.value.password shouldBe ""
        viewModel.registroEstado.value.nombre shouldBe ""
        viewModel.registroEstado.value.email shouldBe ""
    }

    "registro vacio marca todos los campos obligatorios" {
        val viewModel = crearViewModel()

        viewModel.registrarFormulario()

        val errores = viewModel.registroEstado.value.errores

        errores.nombre shouldBe "El nombre es obligatorio"
        errores.email shouldBe "El email es obligatorio"
        errores.clave shouldBe "La contraseña es obligatoria"
        errores.confirmarClave shouldBe "Confirma la contraseña"
    }

    "registro rechaza email invalido" {
        val viewModel = crearViewModel(
            emailValido = { false }
        )

        viewModel.onRegistroNombreChange("Usuario")
        viewModel.onRegistroEmailChange("correo-malo")
        viewModel.onRegistroClaveChange("12345")
        viewModel.onRegistroConfirmarClaveChange("12345")

        viewModel.registrarFormulario()

        viewModel.registroEstado.value.errores.email shouldBe
                "Ingresa un email válido"
    }

    "registro detecta contraseñas diferentes" {
        val viewModel = crearViewModel()

        viewModel.onRegistroNombreChange("Usuario")
        viewModel.onRegistroEmailChange("usuario@test.cl")
        viewModel.onRegistroClaveChange("12345")
        viewModel.onRegistroConfirmarClaveChange("54321")

        viewModel.registrarFormulario()

        viewModel.registroEstado.value.errores.confirmarClave shouldBe
                "Las contraseñas no coinciden"
    }

    "editar campos de registro limpia sus errores" {
        val viewModel = crearViewModel()

        viewModel.registrarFormulario()

        viewModel.onRegistroNombreChange("Usuario")
        viewModel.onRegistroEmailChange("usuario@test.cl")
        viewModel.onRegistroClaveChange("12345")
        viewModel.onRegistroConfirmarClaveChange("12345")

        val errores = viewModel.registroEstado.value.errores

        errores.nombre.shouldBeNull()
        errores.email.shouldBeNull()
        errores.clave.shouldBeNull()
        errores.confirmarClave.shouldBeNull()
    }

    "registro exitoso actualiza estado" {
        val repository = mockk<AuthRepository>(relaxed = true)

        coEvery {
            repository.registrar(
                nombre = "Usuario Nuevo",
                email = "nuevo@test.cl",
                clave = "12345"
            )
        } returns true

        val viewModel = crearViewModel(repository)

        viewModel.onRegistroNombreChange("Usuario Nuevo")
        viewModel.onRegistroEmailChange("nuevo@test.cl")
        viewModel.onRegistroClaveChange("12345")
        viewModel.onRegistroConfirmarClaveChange("12345")

        viewModel.registrarFormulario()

        viewModel.registroEstado.value.registroExitoso shouldBe true
        viewModel.registroEstado.value.error.shouldBeNull()
    }

    "email ya registrado muestra error" {
        val repository = mockk<AuthRepository>(relaxed = true)

        coEvery {
            repository.registrar(
                nombre = "Usuario",
                email = "existente@test.cl",
                clave = "12345"
            )
        } returns false

        val viewModel = crearViewModel(repository)

        viewModel.onRegistroNombreChange("Usuario")
        viewModel.onRegistroEmailChange("existente@test.cl")
        viewModel.onRegistroClaveChange("12345")
        viewModel.onRegistroConfirmarClaveChange("12345")

        viewModel.registrarFormulario()

        viewModel.registroEstado.value.registroExitoso shouldBe false

        viewModel.registroEstado.value.error shouldBe
                "Este email ya está registrado"
    }

    "limpiarRegistro restaura el formulario" {
        val viewModel = crearViewModel()

        viewModel.onRegistroNombreChange("Usuario")
        viewModel.onRegistroEmailChange("usuario@test.cl")
        viewModel.onRegistroClaveChange("12345")
        viewModel.onRegistroConfirmarClaveChange("12345")

        viewModel.limpiarRegistro()

        viewModel.registroEstado.value.nombre shouldBe ""
        viewModel.registroEstado.value.email shouldBe ""
        viewModel.registroEstado.value.clave shouldBe ""
        viewModel.registroEstado.value.confirmarClave shouldBe ""
        viewModel.registroEstado.value.registroExitoso shouldBe false
        viewModel.registroEstado.value.error.shouldBeNull()
    }
})