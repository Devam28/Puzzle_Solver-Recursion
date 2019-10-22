/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author dell-3542 i5
 */
public class FillInPuzzle {

    int choices; // Stores the number of "steps undone" to solve the particular crossword.
    int global_col; // Stores the number of columns in the crossword.
    int global_row; // Stores the number of rows in the crossword.
    int gl_r; // Will be used to store the row index of previous slot.
    int gl_c; // // Will be used to store the column index of previous slot.
    int gl_len; // // Will be used to store the length of previous slot.
    char gl_orientation; // // Will be used to store the orientation of previous slot.
    ArrayList<ArrayList<Character>> listOfLists = new ArrayList<>(); // ArrayList of ArrayList that stores the input in a matrix format
    ArrayList<String> value_list = new ArrayList<>(); // ArrayList that stores the provided values
    ArrayList<Integer> info_list = new ArrayList<>(); // ArrayList that stores col,row,length of all slot in that same order
    ArrayList<Character> orientation_list = new ArrayList<>(); // Stores the orientation (i.e 'h' for horizontal and 'v' for vertical
    ArrayList<ArrayList<Character>> crossword_board = new ArrayList<>(); // Stores the final solved crossword
    ArrayList<ArrayList<Integer>> ref_board = new ArrayList<>(); // Stores the referece board used, while comparing characters.

    boolean loadPuzzle(BufferedReader stream) { // Method to load the puzzle.
        try {
            int col = Character.getNumericValue(stream.read()); // Read the column number
            global_col = col;
            stream.read(); // Ignore the whitespace
            int row = Character.getNumericValue(stream.read()); // Read the row number
            global_row = row;
            stream.read();
            int words = Character.getNumericValue(stream.read()); // Read total number of words.
            stream.readLine();
            int count_1 = 0;
            for (int i = 0; i < words; i++) { // Loop to add number of arrayList rows
                listOfLists.add(new ArrayList<>());
            }
            for (int i = 0; i < words; i++) {
                String line = stream.readLine();
                line = line.replace(" ", ""); // Replaces whitspaces with no spaces

                for (int j = 0; j < 4; j++) {
                    listOfLists.get(i).add(j, line.charAt(j)); // Add only numbers (not whitespaces) tolist
                }
                count_1++;
            }
            String st;
            st = stream.readLine();
            int count_2;
            for (count_2 = 0; st != null; count_2++) { // Terminate condition, if number of words and data rows given doesnt match
                if (count_2 >= words) {
                    System.out.println("Incorrect Output");
                    return false; // Terminate
                }
                value_list.add(count_2, st); // Adding words to value list
                st = stream.readLine();
            }
            if (!(count_2 == words) && !(count_1 == row)) {
                System.out.println("Incorrect Output");
                return false;
            }
            System.out.println("Puzzle:- "); // Printing the puzzle
            System.out.println();
            for (int i = 0; i < words; i++) {
                for (int j = 0; j < 4; j++) {
                    System.out.print(listOfLists.get(i).get(j) + " ");
                }
                System.out.println();
            }
            for (int i = 0; i < value_list.size(); i++) { //Printing the value list.
                System.out.println(value_list.get(i));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return true;
    }

    boolean solve() {
        for (int i = 0; i < global_row; i++) { // Instantitate crossword board and reference board.
            crossword_board.add(new ArrayList<>());
            ref_board.add(new ArrayList<>());
        }

        for (int i = 0; i < crossword_board.size(); i++) { // Initialize crossword and reference board.
            for (int j = 0; j < global_col; j++) {
                crossword_board.get(i).add(' ');
                ref_board.get(i).add(0);
            }
        }
        for (int i = 0; i < value_list.size(); i++) { // Loop to convert bottom left corner as the starting point.
            int sample = Integer.parseInt(listOfLists.get(i).get(1).toString());
            int result = global_row - sample - 1;
            int Base = 36;
            char result_final = Character.forDigit(result, Base); // Manipulating indices according to question.
            listOfLists.get(i).set(1, result_final);

        }
        for (int i = 0; i < value_list.size(); i++) {
            for (int j = 0; j < 4; j++) {
                if (j == 3) {
                    orientation_list.add(listOfLists.get(i).get(j)); // Populating the orientation list ('h' or 'v')
                } else {
                    info_list.add(Integer.parseInt(listOfLists.get(i).get(j).toString())); // Populate the information list

                }
            }

        }

        recursion1(); // Call the recursive function

        return true;
    }

    boolean recursion1() {
        if (info_list.isEmpty()) { // Terminating condition when there is no element left.
            return true;
        }
        int slot_c = info_list.remove(0); // Removing col index of first slot
        int slot_r = info_list.remove(0); // removing row index of first slot
        int length = info_list.remove(0); // Removing length of first slot
        char orientation = orientation_list.remove(0); // Removing orientation of first slot.
        for (int i = 0; i < value_list.size(); i++) {
            if (value_list.get(i).length() == length) {
                String sample = value_list.get(i); // Storing current iteration value for later use.
                int temp = slot_c; // temp variables used during traversing , according to 'h' or 'v' orientation.
                int temp2 = slot_r;

                if (orientation == 'h') { // When orientation is horizontal
                    int flag = 0; // temp variable that decides whether or not to write it to crossword.

                    for (int j = 0; j < length; j++) { // Loop to traverse for conflicts
                        char charAt = value_list.get(i).charAt(j); // Take each character of a particular word and match.
                        // Condition when there is a conflict 
                        if (ref_board.get(slot_r).get(temp) == 1 && crossword_board.get(slot_r).get(temp) != charAt) {
                            break;
                        } else { // If no conflict found 
                            flag = flag + 1;
                        }
                        temp++;
                    }

                    if (flag == length) { // Condition to write the word on the crossword.
                        temp = slot_c;
                        for (int j = 0; j < length; j++) { // Looping to write word to crossword and set reference board.
                            char charAt = value_list.get(i).charAt(j);
                            crossword_board.get(slot_r).set(temp, charAt);
                            ref_board.get(slot_r).set(temp, 1);
                            temp++;
                        }

                        value_list.remove(i); // Removing the word from valuelist.
                        // If recursion fails add back the slot data and set nullify other changes
                        if (!recursion1() && value_list.size() > 0) {
                            choices++;
                            value_list.add(i, sample);
                            temp = slot_c;
                            for (int j = 0; j < length; j++) {
                                crossword_board.get(slot_r).set(temp, ' ');
                                ref_board.get(slot_r).set(temp, 0);
                                temp++;
                            }
                            info_list.add(0, gl_len); // Adding all slot related details back to info_list 
                            info_list.add(0, gl_r);
                            info_list.add(0, gl_c);
                            orientation_list.add(0, gl_orientation);
                        }
                    }
                } else if (orientation == 'v') { // Condition when orientation is vertical 'v'.
                    int flag = 0; // temp variable that decides whether or not to write it to crossword.
                    for (int j = 0; j < length; j++) { // Loop to traverse for conflicts
                        char charAt = value_list.get(i).charAt(j);
                        if (ref_board.get(temp2).get(slot_c) == 1 && crossword_board.get(temp2).get(slot_c) != charAt) {
                            break;
                        } else {
                            flag = flag + 1;
                        }
                        temp2++;
                    }
                    if (flag == length) { // Condition to write the word on the crossword.
                        temp2 = slot_r;
                        for (int j = 0; j < length; j++) {
                            char charAt = value_list.get(i).charAt(j);
                            crossword_board.get(temp2).set(slot_c, charAt);
                            ref_board.get(temp2).set(slot_c, 1);
                            temp2++;
                        }
                        value_list.remove(i);
                        if (!recursion1()) {
                            if (value_list.size() > 0) {
                                value_list.add(i, sample);
                                temp2 = slot_r;
                                for (int j = 0; j < length; j++) {
                                    crossword_board.get(temp2).set(slot_c, ' ');
                                    ref_board.get(temp2).set(slot_c, 0);

                                    temp2++;
                                }
                                info_list.add(0, gl_len);
                                info_list.add(0, gl_r);
                                info_list.add(0, gl_c);
                                orientation_list.add(0, gl_orientation);
                            }
                        }
                    }
                }
            }
        }
        // Setting the value of current slot to a global value (Used when recursion fails and we have to add back the slot details.
        gl_c = slot_c;
        gl_r = slot_r;
        gl_len = length;
        gl_orientation = orientation;

        return false;
    }

    int choices() { // Counts to number of undo changes performed
        return choices;
    }

    void print(PrintWriter outstream) { // FUnction to print crossword to outstream
        for (int i = 0; i < crossword_board.size(); i++) {
            for (int j = 0; j < crossword_board.get(i).size(); j++) {
                outstream.write(crossword_board.get(i).get(j));

            }
            outstream.write("\n");
        }
        outstream.close();
    }

}
