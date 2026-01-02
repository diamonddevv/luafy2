package dev.diamond.luafy;

public class HelloWorldSupplier {
    /**
     * Quirky class to supply a random Hello World for the initialisation string.
     * To contributors; feel free to add as many as you like! They are all literally just "hello, world!" in a given language.
     * Please add the source language next to it :P conlangs are welcome :)
     */

    private static String[] HELLO_WORLD = new String[] {
            "Hello, world!",        // english
            "Hei, maailma!",        // finnish
            "Hallo, Welt!",         // german
            "Bonjour, le monde!",   // french
            "Tere, maailm!",        // estonian
            "Hallo, wereld!",       // dutch
            "Saluton, mondo!",      // esperanto
            "Witaj, świecie!",      // polish
            "Hej, världen!",        // swedish
            "¡Hola, mundo!",        // spanish
    };

    public static String supply(long time) {
        // i cba using randomness lets use math
        return HELLO_WORLD[(int) (time % HELLO_WORLD.length)];
    }
}
