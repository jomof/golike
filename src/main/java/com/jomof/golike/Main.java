package com.jomof.golike;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

// http://www.utf8-chartable.de/unicode-utf8-table.pl?start=9472&number=1024
public class Main {
    public static void main(String[] args) throws IOException {
        Board board = Board.getStandard();

        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);

        screen.startScreen();

        // Create panel to hold components
        int size = 10;
        Panel panel = new Panel();
        GridLayout layout = new GridLayout(size);
        layout.setHorizontalSpacing(1);
        layout.setVerticalSpacing(1);
        panel.setLayoutManager(layout);
        for (Character i = 0; i < size * size; ++i) {
            Character c = 0x253c;

            panel.addComponent(new Label(c.toString()));
        }
//
//        panel.addComponent(new Label("Forename"));
//        panel.addComponent(new TextBox());
//
//        panel.addComponent(new Label("Surname"));
//        panel.addComponent(new TextBox());
//
//        panel.addComponent(new EmptySpace(new TerminalSize(0, 0))); // Empty space underneath labels
//        panel.addComponent(new Button("Submit"));

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

        gui.addWindowAndWait(window);


    }
}