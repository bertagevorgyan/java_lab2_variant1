import java.util.*;

public class Trie {
  private static final int ALPHABET_SIZE = 26;
  private static final char FIRST_LETTER = 'a';

  private TrieNode root;
  private int wordCount;

  private static class TrieNode {
    TrieNode[] children;
    boolean isEndOfWord;
    int childCount;

    TrieNode() {
      this.children = new TrieNode[ALPHABET_SIZE];
      this.isEndOfWord = false;
      this.childCount = 0;
    }
  }

  public Trie() {
    this.root = new TrieNode();
    this.wordCount = 0;
  }

  //вставка слова в префиксное дерево
  public void insert(String word) {
    if (word == null || word.isEmpty()) {
      return;
    }

    TrieNode current = root;
    for (int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      int index = charToIndex(c);

      if (index < 0 || index >= ALPHABET_SIZE) {
        continue; // Пропускаем не-буквенные символы
      }

      if (current.children[index] == null) {
        current.children[index] = new TrieNode();
        current.childCount++;
      }
      current = current.children[index];
    }

    if (!current.isEndOfWord) {
      current.isEndOfWord = true;
      wordCount++;
    }
  }

  //проверка наличия слова в дереве 
  public boolean contains(String word) {
    if (word == null || word.isEmpty()) {
      return false;
    }

    TrieNode node = findNode(word);
    return node != null && node.isEndOfWord;
  }

  //проверка существования слов с данным префиксом
  public boolean startsWith(String prefix) {
    if (prefix == null || prefix.isEmpty()) {
      return false;
    }

    return findNode(prefix) != null;
  }

  //получение всех слов по префиксу 
  public String[] getByPrefix(String prefix) {
    if (prefix == null || prefix.isEmpty()) {
      return new String[0];
    }

    TrieNode node = findNode(prefix);
    if (node == null) {
      return new String[0];
    }

    // сначала подсчитаем количество слов
    int count = countWordsInSubtree(node);
    String[] result = new String[count];

    // затем соберем слова
    if (count > 0) {
      WordCollector collector = new WordCollector(result);
      collectWords(node, prefix, collector);
    }

    return result;
  }


  private static class WordCollector {
    private String[] array;
    private int index;

    WordCollector(String[] array) {
      this.array = array;
      this.index = 0;
    }

    void add(String word) {
      if (index < array.length) {
        array[index++] = word;
      }
    }
  }


  private TrieNode findNode(String str) {
    TrieNode current = root;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      int index = charToIndex(c);

      if (index < 0 || index >= ALPHABET_SIZE || current.children[index] == null) {
        return null;
      }
      current = current.children[index];
    }
    return current;
  }

  private void collectWords(TrieNode node, String currentPrefix, WordCollector collector) {
    if (node.isEndOfWord) {
      collector.add(currentPrefix);
    }

    for (int i = 0; i < ALPHABET_SIZE; i++) {
      if (node.children[i] != null) {
        char c = indexToChar(i);
        collectWords(node.children[i], currentPrefix + c, collector);
      }
    }
  }

  private int countWordsInSubtree(TrieNode node) {
    int count = 0;
    if (node.isEndOfWord) {
      count++;
    }

    for (int i = 0; i < ALPHABET_SIZE; i++) {
      if (node.children[i] != null) {
        count += countWordsInSubtree(node.children[i]);
      }
    }
    return count;
  }



  public int size() {
    return wordCount;
  }

  public boolean isEmpty() {
    return root.childCount == 0;
  }

  public String getLongestWord() {
    String[] longest = { "" };
    findLongestWord(root, "", longest);
    return longest[0];
  }

  private void findLongestWord(TrieNode node, String current, String[] longest) {
    if (node.isEndOfWord && current.length() > longest[0].length()) {
      longest[0] = current;
    }

    for (int i = 0; i < ALPHABET_SIZE; i++) {
      if (node.children[i] != null) {
        char c = indexToChar(i);
        findLongestWord(node.children[i], current + c, longest);
      }
    }
  }


  public String[] getAllWords() {
    return getByPrefix("");
  }


  public void visualize() {
    System.out.println("ВИЗУАЛИЗАЦИЯ ПРЕФИКСНОГО ДЕРЕВА:");
    System.out.println("Всего слов: " + wordCount);
    System.out.println("Структура дерева:");
    visualizeNode(root, "", "└── ", true);
  }

  private void visualizeNode(TrieNode node, String prefix, String pointer, boolean isLast) {
    if (node != root) {
      System.out.println(prefix + pointer + (node.isEndOfWord ? " " : "^ ") +
          "[" + countWordsInSubtree(node) + " слов]");
    }

    String newPrefix = prefix + (isLast ? "    " : "│   ");

    //собираем ненулевые дочерние узлы
    int nonNullCount = 0;
    for (int i = 0; i < ALPHABET_SIZE; i++) {
      if (node.children[i] != null) {
        nonNullCount++;
      }
    }

    int currentIndex = 0;
    for (int i = 0; i < ALPHABET_SIZE; i++) {
      if (node.children[i] != null) {
        char key = indexToChar(i);
        boolean lastChild = (++currentIndex == nonNullCount);
        String childPointer = lastChild ? "└── " : "├── ";

        System.out.println(newPrefix + childPointer + "'" + key + "'");
        visualizeNode(node.children[i], newPrefix,
            lastChild ? "    " : "│   ", lastChild);
      }
    }
  }

  //статистика дерева 
  public void printStatistics() {
    System.out.println("\nСТАТИСТИКА ПРЕФИКСНОГО ДЕРЕВА:");
    System.out.println("Общее количество слов: " + wordCount);
    System.out.println("Самое длинное слово: \"" + getLongestWord() + "\"");

    System.out.println("Статистика по первым буквам:");
    int[] firstLetterStats = getFirstLetterStatistics();
    for (int i = 0; i < ALPHABET_SIZE; i++) {
      if (firstLetterStats[i] > 0) {
        char letter = indexToChar(i);
        System.out.println("  '" + letter + "': " + firstLetterStats[i] + " слов");
      }
    }
  }

  private int[] getFirstLetterStatistics() {
    int[] stats = new int[ALPHABET_SIZE];
    for (int i = 0; i < ALPHABET_SIZE; i++) {
      if (root.children[i] != null) {
        stats[i] = countWordsInSubtree(root.children[i]);
      }
    }
    return stats;
  }


  public void searchWithHighlight(String word) {
    System.out.println("\nПоиск слова: \"" + word + "\"");
    TrieNode current = root;
    char[] path = new char[word.length()];
    int pathLength = 0;

    for (int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      int index = charToIndex(c);

      if (index < 0 || index >= ALPHABET_SIZE || current.children[index] == null) {
        System.out.println("Слово не найдено (прервано на символе '" + c + "')");
        System.out.print("Найденный префикс: \"");
        for (int j = 0; j < pathLength; j++) {
          System.out.print(path[j]);
        }
        System.out.println("\"");
        return;
      }

      path[pathLength++] = c;
      current = current.children[index];

      //визуализация шагов по поиску слова 
      System.out.print("Шаг " + (i + 1) + ": \"");
      for (int j = 0; j < pathLength; j++) {
        System.out.print(path[j]);
      }
      System.out.print("\"");

      if (i < word.length() - 1) {
        System.out.print("[");
        for (int j = i + 1; j < word.length(); j++) {
          System.out.print(word.charAt(j));
        }
        System.out.print("]");
      }
      System.out.println();
    }

    if (current.isEndOfWord) {
      System.out.println("Слово найдено! \"" + word + "\"");
    } else {
      System.out.println("Префикс найден, но полного слова нет");
    }
  }


  public void importWords(String[] words) {
    for (int i = 0; i < words.length; i++) {
      insert(words[i]);
    }
  }

  //преобразование между символами и индексами для работы с массивом детей
  private int charToIndex(char c) {
    return Character.toLowerCase(c) - FIRST_LETTER;
  }

  private char indexToChar(int index) {
    return (char) (FIRST_LETTER + index);
  }
}


class TrieDemonstration {
  public static void main(String[] args) {
    Trie trie = new Trie();

    System.out.println("Демонстрация префиксного дерева\n");

    //наши входные данные
    String[] words = {
        "algoritmh", "algebra", "alphabet", "banana", "band",
        "car", "carboard", "dog", "document", "data"
    };
    trie.importWords(words);
    trie.visualize();
    trie.printStatistics();
    trie.searchWithHighlight("algebra");
    trie.searchWithHighlight("document");

    System.out.println("\nВсе слова с префиксом 'al':");
    String[] alWords = trie.getByPrefix("al");
    for (int i = 0; i < alWords.length; i++) {
      System.out.println(alWords[i]);
    }

    System.out.println("\nДопоплнительные операции:");
    System.out.println("Самое длинное слово: \"" + trie.getLongestWord() + "\"");
    System.out.println("Содержит 'algebra': " + trie.contains("algebra"));
    System.out.println("Есть слова на 'b': " + trie.startsWith("b"));

  }
}