package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.extensions.gone
import com.example.myapplication.extensions.show
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Firebase.auth.currentUser != null) {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
            return
        }

        binding.loginButton.setOnClickListener {
            val email = binding.userEmail.text.toString()
            val password = binding.password.text.toString()

            if (email.isNullOrBlank() || password.isNullOrBlank()) {
                Toast.makeText(
                    requireContext(),
                    "username and password can't be null",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            logIn(email, password)
        }

        binding.signupText.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSingUpFragment())
        }

    }

    private fun logIn(email: String, password: String) {
        binding.progress.show()
        binding.loginButton.isEnabled = false

        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                binding.progress.gone()
                binding.loginButton.isEnabled = true

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithEmail:success")
                    val user = Firebase.auth.currentUser
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    //updateUI(null)
                }
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}