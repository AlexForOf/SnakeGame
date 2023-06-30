package Visual;

import Events.*;
import Functional.SaveLoad;
import Functional.Score;
import Gameplay.Connector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Visual extends JFrame implements CollisionEventListener, MovementEventListener {
    private final TableModel model;

    private final JPanel leftPanel;
    public final ControlsPanel controlsPanel;

    private List<Score> leadersScores;
    public Score currentPlayer;

    private DirectionChangedEventListener directionChangedEventListener;
    private RestartEventListener restartEventListener;

    public Visual(Connector connector, ImageIcon[] textures){
        this.setSize(1280, 825);
        this.setLayout(new BorderLayout());

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

        leadersScores = new ArrayList<>();

        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        leftPanel = new ControlsPanel();
        controlsPanel = (ControlsPanel)leftPanel;

        JPanel mainPanel = new JPanel();
        mainPanel.setVisible(true);
        mainPanel.setLayout(new BorderLayout());

        JTable gameField = new JTable();
        gameField.setRowHeight(31);

        JScrollPane scrollPane = new JScrollPane();
        model = new TableModel(connector);
        gameField.setModel(model);
        gameField.setDefaultRenderer(Object.class, new TableAdapter(textures));

        TableColumnModel tableColumnModel = gameField.getColumnModel();
        for (int i = 0; i < tableColumnModel.getColumnCount(); i++) {
            tableColumnModel.getColumn(i).setMaxWidth(40);
        }
        tablePanel.setBackground(new Color(85, 139, 47));
        scrollPane.setViewportView(gameField);
        tablePanel.add(Box.createVerticalGlue());
        tablePanel.add(gameField, BorderLayout.CENTER);
        tablePanel.add(Box.createVerticalGlue());
        mainPanel.add(leftPanel, BorderLayout.LINE_START);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        outerPanel.add(mainPanel, BorderLayout.CENTER);
        this.getContentPane().add(outerPanel);

        this.setResizable(true);
        this.setVisible(true);

        new NewGameDialog(this);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_RELEASED){
                System.out.println(e.getKeyCode());
                DirectionChangedEvent changedDirection = new DirectionChangedEvent(e);
                switch (e.getKeyCode()) {
                    case 37 -> {
                        changedDirection.setDirection(3);//Left
                        directionChangedEventListener.changeDirection(changedDirection);
                    }
                    case 38 -> {
                        changedDirection.setDirection(0);//Up
                        directionChangedEventListener.changeDirection(changedDirection);
                    }
                    case 39 -> {
                        changedDirection.setDirection(1);//Right
                        directionChangedEventListener.changeDirection(changedDirection);
                    }
                    case 40 -> {
                        changedDirection.setDirection(2);//Down
                        directionChangedEventListener.changeDirection(changedDirection);
                    }
                }
            }
            return false;
        });
    }

    public void addDirectionChangedEventListener(DirectionChangedEventListener el){
        this.directionChangedEventListener = el;
    }
    public void addRestartEventListener(RestartEventListener el){
        this.restartEventListener = el;
    }

    private void restart(){
        RestartEvent restartEvent = new RestartEvent(this);
        restartEventListener.restartGame(restartEvent);
    }
    private void start(List<Score> newScores, Score newPlayer){
        this.leadersScores = newScores;
        currentPlayer = newPlayer;
        controlsPanel.setupScoresPanel();
        controlsPanel.revalidate();
        controlsPanel.repaint();
    }

    @Override
    public void collision(CollisionEvent e) {
        ControlsPanel panel = (ControlsPanel) leftPanel;
        new GameOverDialog(this, e.getStatus(), panel.getScore(), panel.getLevel(), leadersScores);
    }
    @Override
    public void updateOnMovement(MovementEvent e) {
        model.fireTableDataChanged();
    }

    private class ControlsPanel extends JPanel implements EatenFoodEventListener, LevelUpEventListener{
        private final PaintingPanel scorePaintingPanel;
        private final PaintingPanel levelPaintingPanel;

        public JPanel leaderboardPanelLeadersList;
        public JPanel leaderboardPanel;

        Font forLabelsFont = new Font("Arial", Font.BOLD, 16);
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        Font numberFont = new Font("Arial", Font.ITALIC, 16);
        Font nameFont = new Font("Arial", Font.BOLD, 18);
        Font buttonFont = new Font("Arial", Font.BOLD, 20);

        EmptyBorder padding = new EmptyBorder(0,5,0,5);
        EmptyBorder leftPad = new EmptyBorder(0,5,0,0);
        EmptyBorder rightPad = new EmptyBorder(0,0,0,5);

        Color scoreColor = new Color(237, 76, 103);
        Color levelColor = new Color(247, 159, 31);
        private ControlsPanel(){

            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            this.setBackground(new Color(69, 90, 100));
            this.setBorder(new EmptyBorder(10, 10, 10, 10));
            this.setPreferredSize(new Dimension(200, 300));

            JPanel leftPanelTitlePanel = new JPanel(new BorderLayout());
            JLabel leftPanelTitleLabel = new JLabel("Snake Game");
            leftPanelTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            leftPanelTitleLabel.setFont(titleFont);
            leftPanelTitlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            leftPanelTitlePanel.add(leftPanelTitleLabel, BorderLayout.CENTER);

            JPanel infoPanel = new JPanel();
            infoPanel.setBorder(padding);
            JPanel infoPanelLabelPanel = new JPanel(new BorderLayout());

            JLabel infoPanelLabel = new JLabel("Game info");
            infoPanelLabel.setFont(buttonFont);
            infoPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
            infoPanelLabelPanel.add(infoPanelLabel, BorderLayout.CENTER);

            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
            infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            JPanel infoPanelScorePanel = new JPanel(new BorderLayout());
            JLabel currentScoreInfo = new JLabel("Score: ");
            currentScoreInfo.setFont(forLabelsFont);
            currentScoreInfo.setBorder(leftPad);
            scorePaintingPanel = new PaintingPanel("0", scoreColor);
            currentScoreInfo.setHorizontalAlignment(SwingConstants.LEFT);

            infoPanelScorePanel.add(currentScoreInfo, BorderLayout.LINE_START);
            infoPanelScorePanel.add(scorePaintingPanel, BorderLayout.LINE_END);

            JPanel infoPanelLevelPanel = new JPanel(new BorderLayout());
            JLabel currentLevelInfo = new JLabel("Level: ");
            currentLevelInfo.setFont(forLabelsFont);
            currentLevelInfo.setBorder(leftPad);
            levelPaintingPanel = new PaintingPanel("1", levelColor);
            currentLevelInfo.setHorizontalAlignment(SwingConstants.LEFT);

            infoPanelLevelPanel.add(currentLevelInfo, BorderLayout.LINE_START);
            infoPanelLevelPanel.add(levelPaintingPanel, BorderLayout.LINE_END);

            JPanel restartGamePanel = new JPanel(new BorderLayout());
            restartGamePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            JButton restartGameButton = new JButton("New Game");
            restartGameButton.setFont(buttonFont);
            restartGameButton.addActionListener(e -> restart());
            restartGamePanel.add(restartGameButton, BorderLayout.CENTER);

            infoPanel.add(infoPanelLabelPanel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            infoPanel.add(infoPanelScorePanel);
            infoPanel.add(infoPanelLevelPanel);

            leaderboardPanel = new JPanel(new BorderLayout());
            leaderboardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 570));
            JPanel leaderboardPanelTitlePanel = new JPanel(new BorderLayout());
            leaderboardPanelTitlePanel.setBorder(new EmptyBorder(5,0,10,0));
            JLabel leaderboardPanelTitleLabel = new JLabel("Leaderboard");
            leaderboardPanelTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            leaderboardPanelTitleLabel.setFont(buttonFont);
            leaderboardPanelTitlePanel.add(leaderboardPanelTitleLabel, BorderLayout.CENTER);


           setupScoresPanel();

            leaderboardPanel.add(leaderboardPanelTitlePanel, BorderLayout.NORTH);

            this.add(leftPanelTitlePanel);
            this.add(Box.createRigidArea(new Dimension(0, 20)));
            this.add(infoPanel);
            this.add(Box.createRigidArea(new Dimension(0, 35)));
            this.add(restartGamePanel);
            this.add(Box.createRigidArea(new Dimension(0, 35)));
            this.add(leaderboardPanel);
        }

        public void setupScoresPanel(){
            if (leaderboardPanelLeadersList != null){
                leaderboardPanel.remove(leaderboardPanelLeadersList);
            }
            leaderboardPanelLeadersList = new JPanel();
            leaderboardPanelLeadersList.setLayout(new BoxLayout(leaderboardPanelLeadersList, BoxLayout.PAGE_AXIS));
            Collections.sort(leadersScores);
            System.out.println(leadersScores);
            for (Score score : leadersScores) {
                JPanel leadersPanel = new JPanel();
                leadersPanel.setBorder(new EmptyBorder(15, 5, 0, 5));
                leadersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                leadersPanel.setLayout(new BorderLayout());
                JLabel leadersName = new JLabel(score.getPlayer());
                leadersName.setFont(nameFont);
                leadersName.setBorder(leftPad);

                JLabel leadersScore = new JLabel(Integer.toString(score.getScore()));
                leadersScore.setFont(numberFont);
                leadersScore.setBorder(rightPad);


                leadersPanel.add(leadersName, BorderLayout.LINE_START);
                leadersPanel.add(leadersScore, BorderLayout.LINE_END);
                leaderboardPanelLeadersList.add(leadersPanel);
                leaderboardPanelLeadersList.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            leaderboardPanel.add(leaderboardPanelLeadersList, BorderLayout.CENTER);
        }
        public String getScore(){
            return scorePaintingPanel.getScore();
        }
        public String getLevel(){
            return levelPaintingPanel.getScore();
        }

        @Override
        public void foodEaten(EatenFoodEvent e) {
            scorePaintingPanel.repaintScore(Integer.toString(e.getNewScore()));
        }
        @Override
        public void increaseLevel(LevelUpEvent e) {
            levelPaintingPanel.repaintScore(Integer.toString(e.getNewLevel()));
        }
        private class PaintingPanel extends JPanel{
            private String score;
            private int offsetX;

            private final int offsetY;
            private final Color paintingColor;

            private PaintingPanel(String initialNumber, Color initialColor){
                this.score = initialNumber;
                this.offsetX = 15 + (score.length() - 1) * 10;
                this.offsetY = 20;
                this.paintingColor = initialColor;
                this.setPreferredSize(new Dimension(100, 20));
            }

            public void repaintScore(String newScore){
                score = newScore;
                offsetX = 15 + (score.length() - 1) * 10;
                repaint();
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(paintingColor);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString(score, this.getWidth()-offsetX, offsetY);
            }

            public String getScore(){
                return score;
            }
        }
    }
    private static abstract class GameDialog extends JDialog{
        protected Visual visual;
        protected GridBagConstraints gbc;

        protected JPanel titlePanel;
        protected JLabel titleLabel;
        protected JButton newGameButton;
        protected JButton exitButton;

        private GameDialog(Frame parent){
            super(parent);
            this.setModal(true);
            this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            visual = (Visual) parent;

            titlePanel = new JPanel(new BorderLayout());
            titleLabel = new JLabel("Title");
            titlePanel.add(titleLabel,BorderLayout.CENTER);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            this.setLayout(new GridBagLayout());
            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);

            gbc.anchor = GridBagConstraints.PAGE_START;
            gbc.weightx = 1.0;
            gbc.gridwidth = 2;
            this.add(titlePanel, gbc);

            newGameButton = new JButton("New Game");
            exitButton = new JButton("Exit");


        }
        protected JPanel createButtonPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            panel.add(newGameButton);
            panel.add(exitButton);
            return panel;
        }

    }
    private static class NewGameDialog extends GameDialog{

        private NewGameDialog(Frame parent){
            super(parent);
            this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            titleLabel.setText("New Game");

            JPanel nameSpecifyPanel = new JPanel(new BorderLayout());
            JLabel nameSpecifyLabel = new JLabel("What's your name?");
            nameSpecifyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameSpecifyPanel.add(nameSpecifyLabel, BorderLayout.CENTER);

            JPanel namePanel = new JPanel(new BorderLayout());
            JTextField nameField = new JTextField("");
            namePanel.add(nameField, BorderLayout.CENTER);

            newGameButton.addActionListener(e ->{
                List<Score> list = SaveLoad.loadScores();
                visual.start(list, new Score(nameField.getText(),0));
                dispose();
            });

            exitButton.addActionListener(e ->{
                System.exit(0);
                dispose();
            });

            gbc.gridy = 1;
            this.add(nameSpecifyPanel, gbc);

            gbc.gridy = 2;
            gbc.gridwidth = 1;
            this.add(namePanel, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.PAGE_END;
            gbc.weighty = 1.0;
            this.add(createButtonPanel(), gbc);

            this.pack();
            this.setLocation(
                    (Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2,
                    (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2
            );
            this.setVisible(true);
            this.setLocationRelativeTo(parent);
        }
    }
    private static class GameOverDialog extends GameDialog{
        private GameOverDialog(Frame parent, int status, String score, String level, List<Score> leaderScores){
            super(parent);
            visual.currentPlayer.setScore(Integer.parseInt(score));

            titleLabel.setText("Game Over");

            JPanel statusPanel = new JPanel(new BorderLayout());
            JLabel statusLabel = new JLabel("You've collided with " + (status == 1 ? "the wall" : "your body"));
            statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
            statusPanel.add(statusLabel, BorderLayout.CENTER);

            JLabel scoreLabel = new JLabel("Score");
            JLabel scoreValueLabel = new JLabel(score);

            JLabel levelLabel = new JLabel("Level");
            JLabel levelValueLabel = new JLabel(level);

            JPanel leadersTitlePanel = new JPanel(new BorderLayout());
            JLabel leadersTitleLabel = new JLabel("Top 10 players");
            leadersTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            leadersTitlePanel.add(leadersTitleLabel, BorderLayout.CENTER);

            newGameButton.addActionListener(e ->{
                visual.restart();
                SaveLoad.saveScore(leaderScores);
                List<Score> list = SaveLoad.loadScores();
                visual.start(list, visual.currentPlayer);
                dispose();
            });

            exitButton.addActionListener(e ->{
                leaderScores.add(visual.currentPlayer);
                SaveLoad.saveScore(leaderScores);
                System.exit(0);
                dispose();
            });

            gbc.anchor = GridBagConstraints.PAGE_START;
            gbc.weightx = 1.0;
            gbc.gridwidth = 2;
            this.add(titlePanel, gbc);

            gbc.gridy = 1;
            this.add(statusPanel, gbc);

            gbc.gridy = 2;
            gbc.gridwidth = 1;
            this.add(createCentralPanel(scoreLabel, scoreValueLabel), gbc);

            gbc.gridx = 1;
            this.add(createCentralPanel(levelLabel, levelValueLabel), gbc);



            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            this.add(leadersTitlePanel, gbc);

            JPanel leadersPanel = new JPanel(new BorderLayout());
            JPanel leadersList = createLeaderboard(leaderScores);

            leadersPanel.add(leadersList, BorderLayout.CENTER);
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            this.add(leadersPanel, gbc);

            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.PAGE_END;
            gbc.weighty = 1.0;
            this.add(createButtonPanel(), gbc);

            this.pack();
            this.setLocation(
                    (Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2,
                    (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2
            );
            this.setVisible(true);
            this.setLocationRelativeTo(parent);
        }
        private JPanel createCentralPanel(JLabel label, JLabel valueLabel) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(label, BorderLayout.PAGE_START);
            panel.add(valueLabel, BorderLayout.PAGE_END);
            return panel;
        }
        private JPanel createLeaderboard(List<Score> leaderScores){
            JPanel leadersList = new JPanel();
            leadersList.setLayout(new BoxLayout(leadersList, BoxLayout.PAGE_AXIS));
            for (int i = 0; i < leaderScores.size(); i++) {
                if (i == 10) break;
                JPanel partialPanel = new JPanel(new BorderLayout());
                JPanel leftPart = new JPanel(new BorderLayout());
                JPanel rightPart = new JPanel(new BorderLayout());

                JLabel place = new JLabel(i + 1 + ".");
                JLabel nickname = new JLabel(leaderScores.get(i).getPlayer());
                JLabel scores = new JLabel(Integer.toString(leaderScores.get(i).getScore()));

                leftPart.add(place, BorderLayout.LINE_START);
                leftPart.add(nickname, BorderLayout.CENTER);

                rightPart.setBorder(new EmptyBorder(0,0,0,5));
                rightPart.add(scores, BorderLayout.LINE_END);


                partialPanel.add(leftPart, BorderLayout.LINE_START);
                partialPanel.add(rightPart, BorderLayout.LINE_END);
                leadersList.add(partialPanel);
                leadersList.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            return leadersList;
        }
    }
}

class TableModel extends AbstractTableModel{
    private final Connector connector;

    public TableModel(Connector connector){
        this.connector = connector;
    }

    @Override
    public int getRowCount() {
        return connector.getField().length;
    }

    @Override
    public int getColumnCount() {
        return connector.getField()[0].length;
    }

    @Override
    public String getColumnName(int column) {
        return null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return connector.getField()[rowIndex][columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
    }
}

class TableAdapter extends DefaultTableCellRenderer implements TableCellRenderer {
    private final ImageIcon[] textures;
    private final Color[] fieldColors = {new Color(100, 221, 23), new Color(118, 255, 3)};

    public TableAdapter(ImageIcon[] textures){
        this.textures = textures;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (row % 2 == 0){
            this.setBackground((column%2 == 0) ? fieldColors[1] : fieldColors[0]);
        }else{
            this.setBackground((column%2 == 0) ? fieldColors[0] : fieldColors[1]);
        }
        if ((int)value == 0){
            this.setIcon(null);
        }else{
            this.setBorder(null);
            this.setIconTextGap(0);
            this.setIcon(textures[((int) (value)) - 1]);
            this.setText(null);
        }
        return this;
    }
}