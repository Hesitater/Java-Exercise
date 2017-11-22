package PolynomialCalculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.Array;
import java.util.Scanner;

class StringChecker{
	private String originalString;
	
	public StringChecker(String originalString){
		this.originalString = originalString;
	}
	
	public String getOriginalString(){
		return this.originalString;
	}
	
	//去除字符串originalString的空格
	public void removeSpace(){
		this.originalString = this.originalString.replace(" ", "");
	}
	
	//检查去除空格后字符串originalString的语法
	public boolean checkSyntax() {
		//统一规定第一个多项式前需要加减号
		if (this.originalString.isEmpty()) {
			System.out.println("No input. Program ended.");
			System.exit(0);
		}
		if (this.originalString.charAt(0) == '{') {
			this.originalString = '+' + this.originalString;
		}
		
		//循环匹配输入是否合法
		int polynomialsCount = 0;
		String tempString = new String();
		
		Pattern pattern = Pattern.compile("[+-]\\{(\\([+-]?\\d{1,6}\\,\\d{1,6}\\)\\,){0,49}\\([+-]?\\d{1,6}\\,\\d{1,6}\\)\\}");
		Matcher matcher = pattern.matcher(this.originalString);
		
		while (matcher.find()){
			polynomialsCount++;

			if (polynomialsCount > 20) {
				System.out.println("Amount of polynomials more than 20. Program ended."); //多项式多于20个，报错
				System.exit(0);
			}
			
			if (polynomialsCount == 0) {
				tempString = matcher.group();
			}else{
				tempString = tempString + matcher.group();
			}
		}
		
		if (!tempString.contentEquals(this.originalString)) {
			System.out.println("Input error. Program ended."); //多项式多于20个，报错
			System.exit(0);
		}
		
		return true;
	}
}

class Calculator{
	private String expression;
	private int[] resultArray;
	
	public Calculator(String expression) {
		this.expression = expression;
		
		resultArray = new int[1000000];
		for (int i = 0; i < resultArray.length; i++) {
			resultArray[i] = 0;
		}
	}
	
	//根据公式计算结果并输出
	public void calculate() {
		String[] polynomials;
		String[] tempArray;
		int tempIndex;
		int[] powArray = new int[51];
		polynomials = this.expression.split("}");
		char tempOperation;
		
		//初始化powArray
		for (int i = 0; i < powArray.length; i++) {
			powArray[i] = 0;
		}
		
		//开始以多项式为单位进行计算
		for (int i = 0; i < polynomials.length; i++) {
			tempOperation = polynomials[i].charAt(0);
			polynomials[i] = polynomials[i].replaceFirst("[+-]", "");
			polynomials[i] = polynomials[i].replace("{", "");
			polynomials[i] = polynomials[i].replace("(", "");
			polynomials[i] = polynomials[i].replace(")", "");
			tempArray = polynomials[i].split(",");
			
			//以项为单位进行计算，并检查是否有重复的幂
			for (int j = 0, k = 0; j < tempArray.length; j = j + 2) {
				tempIndex = Integer.parseInt(tempArray[j+1]);
				if (tempOperation == '+') {
					this.resultArray[tempIndex] = this.resultArray[tempIndex] + Integer.parseInt(tempArray[j]);
				}else{
					this.resultArray[tempIndex] = this.resultArray[tempIndex] - Integer.parseInt(tempArray[j]);
				}
				
				for (int l = 0; l < k; l++) {
					if (tempIndex == powArray[k]) {
						System.out.println("Duplicating power is not allowed. Program ended");
						System.exit(0);
					}
				}
				k++;
				powArray[k] = tempIndex;
			}
		}
		
		//输出计算结果
		System.out.print("{");
		for (int i = 0, k = 0; i < this.resultArray.length; i++) {
			if (this.resultArray[i] != 0) {
				if (k == 0) {
					System.out.print("("+ resultArray[i] + "," + i + ")");
					k = 1;
				}else{
					System.out.print(",("+ resultArray[i] + "," + i + ")");
				}
			}
		}
		System.out.print("}");
	}
}


public class PolynomialTest {
	public static void main(String args[]) {
		try {
			Scanner scanner = new Scanner(System.in);
			StringChecker checker = new StringChecker(scanner.nextLine());
			checker.removeSpace();
			checker.checkSyntax();
			Calculator calculator = new Calculator(checker.getOriginalString());
			calculator.calculate();
		} catch (Exception e) {
			System.out.print(e);
		}
	}
}
