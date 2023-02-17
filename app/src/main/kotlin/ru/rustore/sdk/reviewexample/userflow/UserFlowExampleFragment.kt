package ru.rustore.sdk.reviewexample.userflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.rustore.sdk.reviewexample.R
import ru.rustore.sdk.reviewexample.databinding.FragmentUserFlowExampleBinding
import ru.rustore.sdk.reviewexample.userflow.model.UserFlowEvent
import ru.rustore.sdk.reviewexample.userflow.model.UserFlowState

/*
* Представим, что у нас есть игра, где нужно нажать 5 раз кнопку, чтобы победить.
*
* Рекомендуется использовать requestReviewFlow() за 0-3 минуты до показа шторки оценки
* (в данном случае на старте экрана),
* а launchReviewFlow() - в конце флоу пользователя (победа в игре),
* при этом продолжать флоу приложения не в зависимости от поставленной оценки (в любом случае показываем диалог).
* */
class UserFlowExampleFragment : Fragment() {

    private val viewModel: UserFlowExampleViewModel by viewModels()

    private var binding: FragmentUserFlowExampleBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserFlowExampleBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init(requireContext())

        binding?.initViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { state ->
                    binding?.updateState(state)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.event
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { event ->
                    handleEvent(event)
                }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun FragmentUserFlowExampleBinding.initViews() {
        counterSubtitle.text = getString(
            R.string.user_flow_counter_subtitle,
            viewModel.counterWinCondition
        )
        counterValue.setOnClickListener {
            viewModel.onCounterClick()
        }
    }

    private fun FragmentUserFlowExampleBinding.updateState(state: UserFlowState) {
        counterValue.text = state.counterValue.toString()

        if (state.hasWon) {
            counterValue.isEnabled = false
            userFlowEnd.isVisible = true
        }
    }

    private fun handleEvent(event: UserFlowEvent) {
        when (event) {
            // Вне зависимости от результата запуска UI оценки,
            // продолжаем флоу приложения
            UserFlowEvent.ReviewEnd -> {
                val context = context ?: return

                val dialogBuilder = MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.user_flow_restart_title)
                    .setMessage(R.string.user_flow_restart_message)
                    .setPositiveButton(R.string.user_flow_restart_positive_button) { dialog, _ ->
                        // Перезапускаем экран
                        findNavController().navigate(R.id.actionRestartUserFlowExampleFragment)
                        dialog.dismiss()
                    }
                dialogBuilder.show()
            }
        }
    }
}
