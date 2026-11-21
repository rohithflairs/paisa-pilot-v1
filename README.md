# Paisa Pilot 💸✈️

> **Native, 100% Offline Money Tracker for Android**  
> *Package ID:* `com.paisapilot11819`  
> *Target:* Android 15 (API 35) · *Min SDK:* Android 8.0 (API 26)  
> *Currency:* Indian Rupee (₹) stored in paise (`Long`)

---

## 1. Overview & Architecture

Paisa Pilot is built strictly according to the product specification with **zero network dependencies**, **no internet permissions**, and complete local privacy.

### Tech Stack
- **Language:** Kotlin 2.1.0
- **UI Toolkit:** Jetpack Compose (Material 3) with edge-to-edge support
- **Local Database:** Room 2.6.1 (SQLite) with foreign keys, cascade deletes, and unique indices
- **State Management:** Android ViewModel, Kotlin Coroutines, and reactive `StateFlow`
- **Integer Minor-Unit Storage:** All money values are stored as `Long` (paise, e.g. ₹100.50 = `10050L`) to eliminate floating-point rounding errors.
- **Privacy First:** The `android.permission.INTERNET` permission is completely omitted from `AndroidManifest.xml`. No analytics, no cloud tracking, no third-party telemetry.

---

## 2. Core Arithmetic & Rules

| Display Value | Formula |
|---|---|
| **Monthly Funds for Planning** | `Opening Balance + Sum(Add Money)` |
| **Unallocated Budget** | `Monthly Funds − Sum(Category Budgets)` |
| **Net Spending this Month** | `Manual Expenses + Posted Recurring Deductions − Refunds` |
| **Main Balance Remaining** | `Opening Balance + Money Added − Expenses − Recurring Deductions + Refunds` |
| **Category Balance Remaining** | `Category Budget − Category Expenses + Category Refunds` |
| **Daily / Weekly Remaining** | `Period Limit − Non-Recurring Period Spending` |
| **Overspend Display** | When negative, displays absolute value in red (e.g., `Food · ₹50 over budget`). At ₹0, displays `Limit reached`. |

### Important Logic Highlights
1. **Recurring Deductions:** Excluded from daily and weekly purchase limits so regular fixed bills (e.g. rent) do not consume everyday purchase limits.
2. **Top-ups (Add Money):** Adds to monthly unallocated funds without artificially inflating category budgets. If a top-up is reduced or deleted, the app detects if allocated budgets exceed remaining funds and displays a prominent reallocation warning banner.
3. **Refunds:** Linked to the original expense ID, credits the main cash balance, and reduces net spending in the purchase category.
4. **Deterministic Recurrence Engine:** Deduplications enforced via composite unique index `(ruleId, yearMonth)`. Automatically clamps due days for shorter months (e.g. February 28).

---

## 3. How to Build & Download the Final `.apk` and `.aab`

The project includes an automated cloud build pipeline via **GitHub Actions** (`.github/workflows/build-apk.yml`).

### Steps to get your APK in 3 minutes (Zero local Android SDK needed):
1. **Initialize Git & Push to your GitHub repo:**
   ```bash
   cd "C:\Users\a.rohithsai\Documents\Money Tracker App"
   git init
   git add .
   git commit -m "Initial Paisa Pilot release"
   git branch -M main
   git remote add origin https://github.com/<your-username>/paisa-pilot.git
   git push -u origin main
   ```
2. Open your GitHub repository in your browser and click on the **Actions** tab.
3. You will see the **"Build Paisa Pilot Android App"** workflow running.
4. Once completed (approx. 2–3 minutes), click on the build run:
   - Under **Artifacts**, you will see:
     - 📦 **`PaisaPilot-Release-APK`** — Installable `.apk` file for direct installation on your Android phone.
     - 📦 **`PaisaPilot-Release-AAB`** — Signed Android App Bundle ready for Google Play Store publication.

---

## 4. Single-File Theming

To change colors, shapes, animation speeds, or fonts:
1. Open `app/src/main/java/com/paisapilot11819/ui/theme/Theme.kt`.
2. Edit any hex color code or duration.
3. Push to GitHub to receive a rethemed APK!
*(See [`THEMING_GUIDE.md`](THEMING_GUIDE.md) for full instructions and copy-paste presets).*

---

## 5. Offline Data Backup & Restore

Since Paisa Pilot is 100% offline, a full JSON backup and restore feature is built in:
- Go to **Profile → Data Backup & Restore**.
- Tap **"Export JSON"** to save your entire database (plans, categories, budgets, expenses, top-ups, recurring rules) to your phone's storage or Google Drive via Android's native file picker.
- Tap **"Import JSON"** on any device to restore your complete financial history.
