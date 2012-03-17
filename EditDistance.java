/* 
 * EditDistance.java
 *
 * Copyright 2011 Nathan Mock 
 */

import java.util.Scanner;
import java.io.IOException;
import java.util.EnumSet;
import java.awt.Point;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Arrays;

public class EditDistance {
   private static String[] edits = {"Copy","Ins1","Ins2","Ins3","Del1","Del2",
     "Del3","Repl","Prm2","Prm3","ChBl","ChCs"}; 
     
   private static int[] counts = new int[edits.length];
   private static boolean isPrm2, isPrm3;
   private static SubSln sln = new SubSln(new Point(0,0), Edit.COPY, 0, null);
   private static LinkedList<Edit> additionalEdits = new LinkedList();
   private static String s1, s2;
   private static Point currentCell;
   
   private static int minX, minY, numBlanksD1, numBlanksD2;
   private static int bestCost = 0, cost = 0;
   
   public enum Edit {
      INS1 (6), INS2 (6), INS3 (6), DEL1 (3), DEL2 (3), DEL3 (3), 
       REPL (7), COPY (0), PRM2 (1), PRM3 (2), CHBL (0), CHCS(1);

      private final int cost;
      
      Edit (int cost) {
         this.cost = cost;
      }
      
      public int cost() {
         return cost;
      }
   }
   
   private static class SubSln {
      Point position;
      int bestCost;
      Edit bestEdit;
      SubSln from;
      String info;
      
      LinkedList<Edit> additionalEdits = new LinkedList();
      
      public SubSln(Point position, Edit bestEdit, int bestCost, SubSln from) {
         this.position = position;
         this.bestEdit = bestEdit;
         this.bestCost = bestCost;
         this.from = from;
      }
      
      public String toString() {
         return info;
      }
   }

   static SubSln slns[][];
   
   public static void main (String[] args) {
      Scanner in = new Scanner(System.in);
      
      s1 = in.nextLine();
      s2 = in.nextLine();
      
      slns = new SubSln[s1.length()+1][s2.length()+1];
      slns[0][0] = sln;
      
      Arrays.fill(counts,0);
      
      findBestCosts();
      printSlns(); 
   }
   
   private static void findBestCosts() {
      Edit editIterator;
      
      for (int d1 = 0; d1 <= s1.length(); d1++) {
         for (int d2 = 0; d2 <= s2.length(); d2++) {
            currentCell = new Point(d1, d2);
              
            if (d1 != 0 || d2 != 0) {
               bestCost = Integer.MAX_VALUE;
               isPrm2 = false; isPrm3 = false;
               
               minX = d1-1; minY = d2-1;
               if (minX >= 0 && minY >= 0) {
                  if (s1.charAt(minX) == s2.charAt(minY)) {
                     checkEdit(Edit.COPY, currentCell);
                  }
               }
               
               for (int i = 1; i <= 3; i++) { 
                  minX = d1; minY = d2-i;
                  if (minY >= 0) {
                     switch (i) {
                        case 1:  editIterator = Edit.INS1; break;
                        case 2:  editIterator = Edit.INS2; break;
                        default: editIterator = Edit.INS3; break;
                     }
                     
                     checkEdit(editIterator, currentCell);
                  }
               }
               
               for (int i = 1; i <= 3; i++) {
                  minX = d1-i; minY = d2;
                  if (minX >= 0) {
                     switch (i) {
                        case 1:  editIterator = Edit.DEL1; break;
                        case 2:  editIterator = Edit.DEL2; break;
                        default: editIterator = Edit.DEL3; break;
                     }
                     
                     checkEdit(editIterator, currentCell);
                  }
               }
               
               if (d1 > 0 && d2 > 0) {
                  minX = d1-1; minY = d2-1;
                  checkEdit(Edit.REPL, currentCell);

                  minX = d1-2; minY = d2-2;
                  if (minX >= 0 && minY >= 0) {
                     isPermutation("", s1.substring(minX, d1),
                      s2.substring(minY, d2));
                     
                     if (isPrm2) {
                        checkEdit(Edit.PRM2, currentCell);
                     }
                  }

                  minX = d1-3; minY = d2-3;
                  if (minX >= 0 && minY >= 0) {
                     isPermutation("", s1.substring(minX, d1),
                      s2.substring(minY, d2));
                     
                     if (isPrm3) {
                        checkEdit(Edit.PRM3, currentCell);
                     }
                  }
                  
                  checkChangeBlanks(d1, d2);
                  
                  minX = d1-1; minY = d2-1;
                  if (s1.substring(minX, d1).
                   equalsIgnoreCase(s2.substring(minY, d2))) {
                     checkEdit(Edit.CHCS, currentCell);
                  }
               }
            
               updateCounts(sln.bestEdit);
               for (int i = 0; i < additionalEdits.size(); i++) {
                  updateCounts(additionalEdits.get(i));
               }
            
               sln.additionalEdits = additionalEdits;
               slns[d1][d2] = sln;
            }
         }
      }
   }
   
   private static void checkEdit(Edit edit, Point cell) {
      int d1 = (int)cell.getX();
      int d2 = (int)cell.getY();
      cost = edit.cost() + slns[minX][minY].bestCost;
   
      if (cost < bestCost) {
         sln = new SubSln(currentCell, edit, cost, slns[minX][minY]);
         bestCost = cost;
         additionalEdits.clear();
         
         switch (edit) {
            case CHCS: 
               sln.info = "Convert case from '" + s1.substring(minX,d1) +
                "' to '" + s2.substring(minY,d2) + "'"; break;
                
            case PRM3:
               sln.info = "Swap '" + s1.substring(minX,d1) + "' to '" +
                s2.substring(minY,d2) + "'"; break;
                
            case PRM2:    
               sln.info = "Swap '" + s1.substring(minX,d1) + "' to '" +
                s2.substring(minY,d2) + "'"; break;
                
            case REPL:
               sln.info = "Replace '" + s1.substring(minX,d1) + "' with '" +
                s2.substring(d2-1,d2) + "'"; break;
                
            case COPY:
               sln.info = "Copy '" + s1.charAt(minX) + "' unchanged"; break;
               
            case INS1:
               sln.info = "Insert '" + s2.substring(minY,d2) + "'"; break;
               
            case INS2:
               sln.info = "Insert '" + s2.substring(minY,d2) + "'"; break;
               
            case INS3:
               sln.info = "Insert '" + s2.substring(minY,d2) + "'"; break;
               
            case DEL1:
               sln.info = "Delete 1 chars"; break;
               
            case DEL2:
               sln.info = "Delete 2 chars"; break;
               
            case DEL3:
               sln.info = "Delete 3 chars"; break;
         }
      }
      else if (cost == bestCost) {
         additionalEdits.add(edit);
      }
   }
   
   public static void checkChangeBlanks(int d1, int d2) {
      int fromX, fromY;
      
      numBlanksD1 = numBlanks(s1.substring(0, d1));
      numBlanksD2 = numBlanks(s2.substring(0, d2));
      
      if (numBlanksD1 > 0 && numBlanksD2 > 0) {
         for (int i = 0; i < numBlanksD1; i++) {
            for (int j = 0; j < numBlanksD2; j++) {
               fromX = d1 - (numBlanksD1 - i); 
               fromY = d2 - (numBlanksD2 - j);

               if ((numBlanksD1 - i) != (numBlanksD2 - j)) {
                  cost = Edit.CHBL.cost() + slns[fromX][fromY].bestCost;

                  if (cost < bestCost) {
                     sln = new SubSln(currentCell, Edit.CHBL,cost, 
                      slns[fromX][fromY]);
                     bestCost = cost;
                     additionalEdits.clear();
                     
                     sln.info = "Convert " + (numBlanksD1 - i) + " blanks to "
                      + (numBlanksD2 - j) + " blanks"; 
                  }
                  else if (cost == bestCost && 
                   !additionalEdits.contains(Edit.CHBL)) {
                     if(sln.bestEdit != Edit.CHBL){
                        additionalEdits.add(Edit.CHBL);
                     }
                  }
               } 
            }
         }
      }
   }
   
   private static void updateCounts(Edit edit) {
      switch (edit) {
         case COPY: counts[0]++; break; 
         
         case INS1: counts[1]++; break;
         
         case INS2: counts[2]++; break; 
         
         case INS3: counts[3]++; break;
         
         case DEL1: counts[4]++; break; 
         
         case DEL2: counts[5]++; break;
         
         case DEL3: counts[6]++; break;
         
         case REPL: counts[7]++; break; 
         
         case PRM2: counts[8]++; break; 
         
         case PRM3: counts[9]++; break;
         
         case CHBL: counts[10]++; break; 
         
         case CHCS: counts[11]++; break;
         
         default: break;
      }
   }
   
   private static int numBlanks(String string) {
      int num = 0;

      for (int i = 0; i < string.length(); i++) {
         if (string.substring(i, i+1).equals(" ")) {
            num++;
         }
         else {
            num = 0;
         }
      }
      
      return num;
   }
   
   private static void isPermutation(String prefix, String s1, String s2) {
       if ((s1.length() <= 1) && (prefix + s1).equals(s2)) {
            if (s2.length() == 2) {
               isPrm2 = true;
            }
            else if (s2.length() == 3) {
               isPrm3 = true;
            }
       }

       for (int i = 0; i < s1.length(); i++) {
          String newPermutation = s1.substring(0, i) + s1.substring(i + 1);
          isPermutation(prefix + s1.charAt(i), newPermutation, s2);
       }
   }
   
   public static void printSlns() {
      SubSln breadCrumb = slns[s1.length()][s2.length()];
      Stack stack = new Stack();

      System.out.println("Best cost is: " + breadCrumb.bestCost); 
      
      for (int i = 0; i < counts.length; i++) {
         System.out.println(edits[i] + " used " + counts[i] + " times.");
      }
      
      System.out.println();
      
      while (breadCrumb.from != null) {
         stack.push(breadCrumb);
         breadCrumb = breadCrumb.from;
      }
      
      while (!stack.empty()) {
         System.out.println(stack.pop().toString());
      }
      
      System.out.println();
   }
}