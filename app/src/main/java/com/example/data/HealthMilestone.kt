package com.example.data

data class HealthMilestone(
    val title: String,
    val description: String,
    val durationMillis: Long,
    val iconName: String
) {
    fun getProgress(elapsedMillis: Long): Float {
        if (elapsedMillis <= 0) return 0f
        return (elapsedMillis.toDouble() / durationMillis.toDouble()).coerceIn(0.0, 1.0).toFloat()
    }

    fun isCompleted(elapsedMillis: Long): Boolean {
        return elapsedMillis >= durationMillis
    }
}

object HealthMilestoneProvider {
    val milestones = listOf(
        HealthMilestone(
            title = "Heart Rate & Blood Pressure",
            description = "Your heart rate and blood pressure drop back to normal levels.",
            durationMillis = 20 * 60 * 1000L, // 20 mins
            iconName = "Favorite"
        ),
        HealthMilestone(
            title = "Carbon Monoxide Cleared",
            description = "Carbon monoxide levels in your blood drop to normal, increasing blood oxygen levels.",
            durationMillis = 12 * 60 * 60 * 1000L, // 12 hours
            iconName = "Air"
        ),
        HealthMilestone(
            title = "Nicotine Levels Drop",
            description = "Nicotine is completely cleared from your body. Cravings might peak but lung cilia heal.",
            durationMillis = 48 * 60 * 60 * 1000L, // 48 hours
            iconName = "Mood"
        ),
        HealthMilestone(
            title = "Bronchial Tubes Relax",
            description = "Your bronchial tubes relax, making breathing easier. Lung capacity increases.",
            durationMillis = 72 * 60 * 60 * 1000L, // 72 hours
            iconName = "SelfImprovement"
        ),
        HealthMilestone(
            title = "Improved Circulation",
            description = "Your circulation improves, and your lung function increases up to 30%. Walking becomes easier.",
            durationMillis = 14 * 24 * 60 * 60 * 1000L, // 2 weeks
            iconName = "DirectionsRun"
        ),
        HealthMilestone(
            title = "Lung Regeneration",
            description = "Coughing, fatigue, and shortness of breath decrease. Lungs are significantly cleaner.",
            durationMillis = 90 * 24 * 60 * 60 * 1000L, // 3 months
            iconName = "LocalActivity"
        ),
        HealthMilestone(
            title = "Heart Disease Risk Cut",
            description = "Your risk of coronary heart disease is now half that of a smoker.",
            durationMillis = 365 * 24 * 60 * 60 * 1000L, // 1 year
            iconName = "Security"
        )
    )
}
