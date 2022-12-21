package zearch.gram;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrammifierTest {

    @Test
    void grammifyTest() {
        Map<String, Integer> grams = Grammifier.grammify("aBcdEf!gh,ab   cde f;");
        assertEquals(grams.get("abc"), 1);
        assertEquals(grams.get("bcd"), 1);
        assertEquals(grams.get("cde"), 2);
        assertEquals(grams.get("def"), 1);
        assertEquals(grams.get("efg"), 1);
        assertEquals(grams.get("fgh"), 1);
        assertEquals(grams.get("gha"), 1);
        assertEquals(grams.get("hab"), 1);
        assertEquals(grams.get("ab "), 1);
        assertEquals(grams.get("b c"), 1);
        assertEquals(grams.get(" cd"), 1);
        assertEquals(grams.get("de "), 1);
        assertEquals(grams.get("e f"),  1);
        assertEquals(grams.keySet().size(), 13);
    }

    @Test void computeGramScoreTest() {
        String romeSummary = "Rome, Italian Roma, City (pop., 2007 est.: city, 2,705,603; urban agglom., 3,339,000), capital of Italy. It is situated on the Tiber River in the central part of the country. The historical site of Rome on its seven hills was occupied as early as the Bronze Age (c. 1500 BCE), and the city was politically unified by the early 6th century BCE. It became the capital of the Roman Empire (see Roman Republic and Empire). The Romans gradually conquered the Italian peninsula (see Etruscan), extended their dominion over the entire Mediterranean basin (see Punic Wars), and expanded their empire into continental Europe. Under Pompey the Great and Julius Caesar, Rome’s influence was extended over Syria, Jerusalem, Cyprus, and Gaul. After the Battle of Actium, all Roman lands were controlled by Octavian (Augustus), the first Roman emperor. As the imperial capital, Rome became the site of magnificent public buildings, including palaces, temples, public baths, theatres, and stadiums. It reached the peak of its grandeur and ancient population during the late 1st and early 2nd centuries CE. It remained the capital of the Roman Empire until Emperor Constantine the Great dedicated Constantinople (now Istanbul) in 330. By the end of the 6th century, the protection of the city was in the hands of the Roman Catholic Church. The papacy achieved absolute rule in the 15th century. The city flourished during the Renaissance and was the seat of the Papal States. In 1870 it became the capital of a united Italy. It was transformed into a modern capital in the 1920s and ’30s and is Italy’s administrative, cultural, and transportation centre. See also Vatican City.";

        System.out.println(Grammifier.computeGramScore(romeSummary));
    }
}