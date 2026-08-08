# Lumi Home Screen — Layout & Display Logic Specifications

> **Note:** This document defines the exact business rules, criteria, and phase computations for Lumi's 3 Home Screen layouts. These rules will be connected to the ViewModel & Room Database once the data persistence layer is ready.

---

## 1. Overview of the 3 Home Screen Layouts

| Layout | Name | Active Phase | Cycle Day Range (28-Day Cycle) | Key Focus |
|---|---|---|---|---|
| **Layout 1** | Late Luteal / Symptom Grid | `LATE_LUTEAL` | Days 22 – 28 | Tracking PMS symptoms, mood, energy & pain as progesterone drops |
| **Layout 2** | Standard Cycle Ring | `MENSTRUATION`, `FOLLICULAR`, `LUTEAL` | Days 1 – 10, 16 – 21 | General cycle progress, period prediction, quick logging |
| **Layout 3** | High Fertility Dashboard | `FERTILE_WINDOW` | Days 11 – 15 | Peak ovulation window, BBT/LH logging, conception or avoidance guidance |

---

## 2. Cycle Phase Calculation Rules

The `CyclePhase` enum is resolved using the user's average `cycleLength` and `periodLength`.

```kotlin
enum class CyclePhase {
    MENSTRUATION,   // Days 1 to periodLength
    FOLLICULAR,     // Days (periodLength + 1) to (fertileStart - 1)
    FERTILE_WINDOW, // Days (ovulationDay - 3) to (ovulationDay + 1) -> LAYOUT 3
    LUTEAL,         // Days (ovulationDay + 2) to (cycleLength - 7)
    LATE_LUTEAL     // Days (cycleLength - 6) to cycleLength       -> LAYOUT 1
}
```

### Mathematical Formulae

Assuming `cycleLength = 28` and `periodLength = 5`:
- `ovulationDay = cycleLength - 14` = Day 14
- `fertileStart = ovulationDay - 3` = Day 11
- `fertileEnd = ovulationDay + 1` = Day 15
- `lateLutealStart = cycleLength - 6` = Day 22

---

## 3. Layout Resolver Matrix

```kotlin
fun resolveHomeLayout(phase: CyclePhase): HomeLayoutType {
    return when (phase) {
        CyclePhase.FERTILE_WINDOW -> HomeLayoutType.FERTILITY_DASHBOARD // Layout 3
        CyclePhase.LATE_LUTEAL    -> HomeLayoutType.SYMPTOM_GRID       // Layout 1
        else                      -> HomeLayoutType.CYCLE_RING          // Layout 2
    }
}
```

---

## 4. Widget Visibility & Content Rules per Layout

### Layout 1: Late Luteal / Symptom Grid (Days 22–28)

1. **Header Card (`LateLutealHeaderCard`)**
   - **Pill Tag:** `"DAY {cycleDay}"`
   - **Title:** `"Late Luteal Phase"`
   - **Body:** `"Progesterone is dropping. You may notice shifts in energy and mood."`
   - **Progress Bar:** Ratio = `cycleDay / cycleLength` (e.g. 24/28 = 85% filled)

2. **Symptom Grid (`LogSymptomsSection`)**
   - Displays 2-column grid: Energy, Mood, Skin, Digestion, Pain, + Add Custom
   - Tapping toggles symptom selection
   - **CTA Button:** `"Save Daily Log"` (Enabled when ≥1 item selected)

3. **Lumi Insight (Pink Card Variant)**
   - Background tint: Pink/Rose (`SecondaryContainer`)
   - Text derived from historical log analysis around current cycle day
   - Action item box embedded: `"Action: Try increasing magnesium intake today."`

4. **30-Day Trends Card (`ThirtyDayTrendsCard`)**
   - Title: `"30-Day Trends"`
   - Tag: `"MOST FREQUENT"` -> surfaces highest logged symptom in current cycle
   - Bar chart showing symptom occurrence across 4 weeks of cycle

---

### Layout 2: Standard Cycle Ring (Days 1–10, 16–21)

1. **Cycle Ring Arc (`CycleRingCard`)**
   - Circular arc indicator filled to `cycleDay / cycleLength`
   - Center displays `"CYCLE DAY {n}"`
   - Sub-label changes based on active sub-phase:
     - **MENSTRUATION:** `"Period Day {periodDay}"`
     - **FOLLICULAR:** `"Fertile window in ~{daysUntilFertile} days"`
     - **LUTEAL / PMS:** `"Period starts in ~{daysUntilPeriod} days"`

2. **Primary Log Button (`LogFlowButton`)**
   - Displays `"Log Flow"` (with droplet icon) during Menstruation or PMS
   - Displays `"Log Today"` during Follicular or Luteal

3. **Lumi Insight (White Elevated Card)**
   - Elevated surface card with sparkle icon
   - Provides cycle variation & wellness guidance (e.g. exercise, sleep adjustments)

4. **Next 7 Days Strip (`Next7DaysCalendarStrip`)**
   - Horizontal row showing 7 days centered around today
   - Today's date is highlighted with a filled circle
   - Period prediction dots shown beneath predicted flow dates

---

### Layout 3: High Fertility Dashboard (Days 11–15)

1. **Fertility Status Header (`FertilityHeaderCard`)**
   - **Pill Tag:** `"CURRENT STATUS"`
   - **Title:** `"High Fertility Today"` (or `"Peak Fertility"`, `"Ovulation Day"`)
   - **Description:** `"Ovulation expected tomorrow. This is your peak window."`
   - **Action Buttons:**
     - Primary Filled: `"Log BBT"` (Thermometer icon)
     - Secondary Outlined: `"Log LH Result"` (Flask icon)

2. **Daily Insight (Fertility Variant)**
   - Interprets today's BBT spike + cervical mucus logs
   - Suggests optimal conception or safety window based on user goal

3. **Basal Body Temp Line Chart (`BasalBodyTempChartCard`)**
   - Displays BBT line plot across cycle days (CD10 to CD18)
   - Highlights today's reading (e.g. `98.1°`) with filled dot tag

4. **Today's Logs List (`TodaysLogsCard`)**
   - Lists logged cervical mucus, mood, symptoms for today
   - Includes `"+ Add More Logs"` button

5. **Contextual Library Card (`LibraryFeaturedCard`)**
   - Dark banner card presenting relevant reading material
   - Example: `"Understanding LH Surges"` during peak fertile days

---

## 5. Planned Database Integration Strategy (Post-Room Setup)

When Room DB is implemented:
1. `CycleRepository` will observe the latest `PeriodEntity` records to calculate `lastPeriodStartDate`, `averageCycleLength`, and `averagePeriodLength`.
2. `HomeViewModel` will derive `CycleState` reactively using `StateFlow`.
3. The UI will seamlessly render the corresponding layout based on the computed `HomeLayoutType`.
