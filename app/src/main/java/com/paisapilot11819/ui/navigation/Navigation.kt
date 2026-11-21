package com.paisapilot11819.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paisapilot11819.ui.screens.setup.MonthSetupScreen
import com.paisapilot11819.ui.screens.dashboard.DashboardScreen
import com.paisapilot11819.ui.screens.expense.AddExpenseScreen
import com.paisapilot11819.ui.screens.addmoney.AddMoneyScreen
import com.paisapilot11819.ui.screens.refund.AddRefundScreen
import com.paisapilot11819.ui.screens.activity.ActivityLogScreen
import com.paisapilot11819.ui.screens.recurring.RecurringScreen
import com.paisapilot11819.ui.screens.recurring.AddRecurringRuleScreen
import com.paisapilot11819.ui.screens.profile.ProfileScreen
import com.paisapilot11819.ui.screens.profile.MonthHistoryScreen

object Routes {
    const val MONTH_SETUP = "month_setup"
    const val DASHBOARD = "dashboard/{monthPlanId}"
    const val ADD_EXPENSE = "add_expense/{monthPlanId}"
    const val EDIT_EXPENSE = "edit_expense/{expenseId}"
    const val ADD_MONEY = "add_money/{monthPlanId}"
    const val EDIT_ADD_MONEY = "edit_add_money/{addMoneyId}"
    const val ADD_REFUND = "add_refund/{expenseId}"
    const val ACTIVITY_LOG = "activity_log/{monthPlanId}"
    const val RECURRING = "recurring"
    const val ADD_RECURRING_RULE = "add_recurring_rule"
    const val EDIT_RECURRING_RULE = "edit_recurring_rule/{ruleId}"
    const val PROFILE = "profile"
    const val MONTH_HISTORY = "month_history/{monthPlanId}"

    fun dashboard(monthPlanId: Long) = "dashboard/$monthPlanId"
    fun addExpense(monthPlanId: Long) = "add_expense/$monthPlanId"
    fun editExpense(expenseId: Long) = "edit_expense/$expenseId"
    fun addMoney(monthPlanId: Long) = "add_money/$monthPlanId"
    fun editAddMoney(addMoneyId: Long) = "edit_add_money/$addMoneyId"
    fun addRefund(expenseId: Long) = "add_refund/$expenseId"
    fun activityLog(monthPlanId: Long) = "activity_log/$monthPlanId"
    fun editRecurringRule(ruleId: Long) = "edit_recurring_rule/$ruleId"
    fun monthHistory(monthPlanId: Long) = "month_history/$monthPlanId"
}

@Composable
fun PaisaPilotNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MONTH_SETUP
    ) {
        composable(Routes.MONTH_SETUP) {
            MonthSetupScreen(
                onMonthReady = { monthPlanId ->
                    navController.navigate(Routes.dashboard(monthPlanId)) {
                        popUpTo(Routes.MONTH_SETUP) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        composable(
            route = Routes.DASHBOARD,
            arguments = listOf(navArgument("monthPlanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val monthPlanId = backStackEntry.arguments?.getLong("monthPlanId") ?: return@composable
            DashboardScreen(
                monthPlanId = monthPlanId,
                onAddExpense = { navController.navigate(Routes.addExpense(monthPlanId)) },
                onAddMoney = { navController.navigate(Routes.addMoney(monthPlanId)) },
                onViewActivityLog = { navController.navigate(Routes.activityLog(monthPlanId)) },
                onNavigateToRecurring = { navController.navigate(Routes.RECURRING) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToSetup = {
                    navController.navigate(Routes.MONTH_SETUP) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.ADD_EXPENSE,
            arguments = listOf(navArgument("monthPlanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val monthPlanId = backStackEntry.arguments?.getLong("monthPlanId") ?: return@composable
            AddExpenseScreen(
                monthPlanId = monthPlanId,
                expenseId = null,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_EXPENSE,
            arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: return@composable
            AddExpenseScreen(
                monthPlanId = 0, // Will be loaded from expense
                expenseId = expenseId,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.ADD_MONEY,
            arguments = listOf(navArgument("monthPlanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val monthPlanId = backStackEntry.arguments?.getLong("monthPlanId") ?: return@composable
            AddMoneyScreen(
                monthPlanId = monthPlanId,
                addMoneyId = null,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_ADD_MONEY,
            arguments = listOf(navArgument("addMoneyId") { type = NavType.LongType })
        ) { backStackEntry ->
            val addMoneyId = backStackEntry.arguments?.getLong("addMoneyId") ?: return@composable
            AddMoneyScreen(
                monthPlanId = 0,
                addMoneyId = addMoneyId,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.ADD_REFUND,
            arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: return@composable
            AddRefundScreen(
                expenseId = expenseId,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.ACTIVITY_LOG,
            arguments = listOf(navArgument("monthPlanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val monthPlanId = backStackEntry.arguments?.getLong("monthPlanId") ?: return@composable
            ActivityLogScreen(
                monthPlanId = monthPlanId,
                onEditExpense = { expenseId -> navController.navigate(Routes.editExpense(expenseId)) },
                onAddRefund = { expenseId -> navController.navigate(Routes.addRefund(expenseId)) },
                onEditAddMoney = { addMoneyId -> navController.navigate(Routes.editAddMoney(addMoneyId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.RECURRING) {
            RecurringScreen(
                onAddRule = { navController.navigate(Routes.ADD_RECURRING_RULE) },
                onEditRule = { ruleId -> navController.navigate(Routes.editRecurringRule(ruleId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADD_RECURRING_RULE) {
            AddRecurringRuleScreen(
                ruleId = null,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_RECURRING_RULE,
            arguments = listOf(navArgument("ruleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getLong("ruleId") ?: return@composable
            AddRecurringRuleScreen(
                ruleId = ruleId,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onMonthSelected = { monthPlanId -> navController.navigate(Routes.monthHistory(monthPlanId)) },
                onBack = { navController.popBackStack() },
                onNavigateToSetup = {
                    navController.navigate(Routes.MONTH_SETUP) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.MONTH_HISTORY,
            arguments = listOf(navArgument("monthPlanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val monthPlanId = backStackEntry.arguments?.getLong("monthPlanId") ?: return@composable
            MonthHistoryScreen(
                monthPlanId = monthPlanId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
