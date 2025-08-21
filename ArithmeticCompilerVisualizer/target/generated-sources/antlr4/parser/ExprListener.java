// Generated from parser/Expr.g4 by ANTLR 4.13.1
package parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExprParser}.
 */
public interface ExprListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExprParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(ExprParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(ExprParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(ExprParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(ExprParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ToTerm}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterToTerm(ExprParser.ToTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ToTerm}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitToTerm(ExprParser.ToTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ToFactor}
	 * labeled alternative in {@link ExprParser#term}.
	 * @param ctx the parse tree
	 */
	void enterToFactor(ExprParser.ToFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ToFactor}
	 * labeled alternative in {@link ExprParser#term}.
	 * @param ctx the parse tree
	 */
	void exitToFactor(ExprParser.ToFactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link ExprParser#term}.
	 * @param ctx the parse tree
	 */
	void enterMulDiv(ExprParser.MulDivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link ExprParser#term}.
	 * @param ctx the parse tree
	 */
	void exitMulDiv(ExprParser.MulDivContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Int}
	 * labeled alternative in {@link ExprParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterInt(ExprParser.IntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Int}
	 * labeled alternative in {@link ExprParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitInt(ExprParser.IntContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link ExprParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterParens(ExprParser.ParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link ExprParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitParens(ExprParser.ParensContext ctx);
}