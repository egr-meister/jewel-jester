package com.jeweljester.game.data

/**
 * Контент викторины. Темы подобраны под Jewel Jester (самоцветы, шуты,
 * карнавал). Тексты — заготовка, их можно свободно заменить.
 */
data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

data class QuizCategory(
    val id: String,
    val title: String,
    val questions: List<Question>
)

object QuizData {

    val categories: List<QuizCategory> = listOf(
        QuizCategory(
            id = "gem_master",
            title = "Gem Master",
            questions = listOf(
                Question("Which gem is traditionally deep red?", listOf("Ruby", "Emerald", "Sapphire"), 0),
                Question("Emeralds are famous for which color?", listOf("Blue", "Green", "Pink"), 1),
                Question("A sapphire is most classically...?", listOf("Blue", "Red", "Clear"), 0),
                Question("The hardest natural gemstone is the...?", listOf("Pearl", "Diamond", "Opal"), 1),
                Question("Amethyst has which signature hue?", listOf("Purple", "Yellow", "Green"), 0),
                Question("Which gem forms inside an oyster?", listOf("Pearl", "Topaz", "Garnet"), 0),
                Question("Gem weight is measured in...?", listOf("Grams", "Carats", "Ounces"), 1),
                Question("A gem's sparkle from cut is called...?", listOf("Brilliance", "Shadow", "Weight"), 0),
                Question("Which is a green-to-red color-change gem?", listOf("Alexandrite", "Quartz", "Jade"), 0),
                Question("Diamonds are made mostly of...?", listOf("Carbon", "Iron", "Salt"), 0),
            )
        ),
        QuizCategory(
            id = "jesters_court",
            title = "Jester's Court",
            questions = listOf(
                Question("A jester is also known as a court...?", listOf("Fool", "Guard", "Cook"), 0),
                Question("What did jesters mainly do?", listOf("Entertain", "Rule", "Farm"), 0),
                Question("A jester's hat often has...?", listOf("Bells", "A crown", "Feathers only"), 0),
                Question("Jesters served in a royal...?", listOf("Court", "Bank", "Harbor"), 0),
                Question("A jester's staff topped with a head is a...?", listOf("Marotte", "Sceptre", "Wand"), 0),
                Question("Jesters were known for telling...?", listOf("Jokes", "Lies only", "Secrets"), 0),
                Question("Classic jester colors are often...?", listOf("Motley / mixed", "All black", "All white"), 0),
                Question("A jester who juggles is showing...?", listOf("Skill", "Anger", "Sleep"), 0),
                Question("Jesters could mock the king because they were...?", listOf("Licensed fools", "Knights", "Priests"), 0),
                Question("The jester's role is closest to a modern...?", listOf("Comedian", "Judge", "Banker"), 0),
            )
        ),
        QuizCategory(
            id = "carnival_legend",
            title = "Carnival Legend",
            questions = listOf(
                Question("Carnival masks are worn to...?", listOf("Disguise", "Eat", "Sleep"), 0),
                Question("Mardi Gras is famous in which US city?", listOf("New Orleans", "Denver", "Boston"), 0),
                Question("Traditional carnival colors include purple, green and...?", listOf("Gold", "Grey", "Brown"), 0),
                Question("A carnival parade often has...?", listOf("Floats", "Tractors", "Trains"), 0),
                Question("Confetti at carnival is thrown to...?", listOf("Celebrate", "Warn", "Clean"), 0),
                Question("Venice is known for its carnival...?", listOf("Masks", "Skis", "Kites"), 0),
                Question("A domino is a type of carnival...?", listOf("Mask", "Dance", "Drink"), 0),
                Question("Carnival happens before which season?", listOf("Lent", "Summer", "Harvest"), 0),
                Question("Beads are commonly tossed at...?", listOf("Mardi Gras", "Chess", "Auctions"), 0),
                Question("A jester fits carnival because it is...?", listOf("Festive", "Solemn", "Silent"), 0),
            )
        ),
    )

    fun byId(id: String): QuizCategory =
        categories.firstOrNull { it.id == id } ?: categories.first()
}
