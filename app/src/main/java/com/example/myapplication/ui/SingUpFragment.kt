package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.PrefUtil
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSingUpBinding
import com.example.myapplication.extensions.gone
import com.example.myapplication.extensions.show
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SingUpFragment : Fragment() {
    private var _binding: FragmentSingUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            singUpButton.setOnClickListener {
                val email = userEmail.text.toString()
                val password = password.text.toString()
                val password2 = password2.text.toString()

                if (email.isNullOrBlank()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.email_can_not_be_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (password.isNullOrBlank() || password != password2) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.password_is_invalid),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                PrefUtil.userName = email
                PrefUtil.password = password

                singUp(email, password)
            }

            loginText.setOnClickListener {
                findNavController().navigate(SingUpFragmentDirections.actionSingUpFragmentToLoginFragment())
            }
        }
    }

    private fun singUp(email: String, password: String) {
        binding.progress.show()
        binding.singUpButton.isEnabled = false

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                binding.progress.gone()
                binding.singUpButton.isEnabled = true


                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "createUserWithEmail:success")
                    val user = Firebase.auth.currentUser
                    //updateUI(user)
                    findNavController().navigate(SingUpFragmentDirections.actionSingUpFragmentToMainFragment())
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed. ${task.exception.toString()}",
                        Toast.LENGTH_SHORT,
                    ).show()
                    // updateUI(null)
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
            SingUpFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}