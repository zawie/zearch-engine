package zearch.engine.similarity.gram;

import org.junit.jupiter.api.Test;
import zearch.engine.similarity.ISimilarity;

import static org.junit.jupiter.api.Assertions.*;

class GramSimilarityTest {

    @Test
    void test1 () {
        String a = "Philosophy";
        String b = "Philosophy (from Greek: φιλοσοφία, philosophia, 'love of wisdom')[1][2] is the systematized study of general and fundamental questions, such as those about existence, reason, knowledge, values, mind, and language.[3][4][5] Such questions are often posed as problems[6][7] to be studied or resolved. Some sources claim the term was coined by Pythagoras (c. 570 – c. 495 BCE),[8][9] although this theory is disputed by some.[10][11][12] Philosophical methods include questioning, critical discussion, rational argument, and systematic presentation.[13][14][i]\n" +
                "\n" +
                "Historically, philosophy encompassed all bodies of knowledge and a practitioner was known as a philosopher.[15] \"natural philosophy,\" which began as a discipline in ancient India and Ancient Greece, encompasses astronomy, medicine, and physics.[16] [17] For example, Newton's 1687 Mathematical Principles of Natural Philosophy later became classified as a book of physics. In the 19th century, the growth of modern research universities led academic philosophy and other disciplines to professionalize and specialize.[18][19] Since then, various areas of investigation that were traditionally part of philosophy have become separate academic disciplines, and namely the social sciences such as psychology, sociology, linguistics, and economics.\n" +
                "\n" +
                "Today, major subfields of academic philosophy include metaphysics, which is concerned with the fundamental nature of existence and reality; epistemology, which studies the nature of knowledge and belief; ethics, which is concerned with moral value; and logic, which studies the rules of inference that allow one to derive conclusions from true premises.[20][21] Other notable subfields include philosophy of religion, philosophy of science, political philosophy, aesthetics, philosophy of language, and philosophy of mind.";
        double sim =  new GramSimilarity().similarity(a,b);
        assertTrue(sim < 1);
        assertTrue(sim > 0);

    }
}