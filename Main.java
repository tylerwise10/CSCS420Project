package clickbait;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Group;

/**
 * 
 * @author Team ClickBait
 * 
 *         The ClickBait UML editor is a simple UML editing program.
 * 
 *         Intent is to be able to draw boxes and lines to show connection
 *         between those boxes.
 */

public class Main extends Application {

	/**
	 * Editor in which fields of a class box can be entered.
	 */
	ClassBox editor = new ClassBox();

	/**
	 * Draws UI elements, listens for button activity, uses other functions to
	 * draw elements.
	 * 
	 * @param stage
	 *            The highest level JavaFX storage container used to hold
	 *            everything in the program.
	 */
	@Override
	public void start(Stage stage) {

		stage.setTitle("ClickBait UML Editor");
		HBox rootHBox = new HBox();
		VBox menuVBox = new VBox();
		menuVBox.setPrefWidth(200);
		menuVBox.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		menuVBox.setSpacing(10);
		Pane view = new Pane();
		Canvas canvas = new Canvas(view.getWidth(), view.getHeight());
		canvas.widthProperty().bind(view.widthProperty());
		canvas.heightProperty().bind(view.heightProperty());
		final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
		view.getChildren().add(canvas);
		rootHBox.getChildren().addAll(menuVBox, view);

		// Menu creation
		Label menuLabel = new Label("-----------------MENU-----------------");
		Button classButton = createButton("Build Class Box", 200);
		Button generalizationButton = new Button("Generalization");
		generalizationButton.setPrefWidth(200);
		VBox classBoxInput = new VBox();
		classBoxInput.setMaxWidth(200);
		classBoxInput.setMaxHeight(400);
		TextField classText = new TextField(editor.getName());
		classText.setPrefWidth(150);

		/**
		 * Populate the name field of a class box.
		 */
		classText.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(final KeyEvent event) {
				editor.setName(classText.getText());
			}
		});

		TextArea attributeText = new TextArea(editor.getAttributes());
		attributeText.setScaleShape(true);
		attributeText.setWrapText(true);

		/**
		 * Populate the attributes field of the class box.
		 */
		attributeText.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(final KeyEvent event) {
				editor.setAttributes(attributeText.getText());
			}
		});

		TextArea methodText = new TextArea(editor.getMethods());
		methodText.setScaleShape(true);
		methodText.setWrapText(true);

		/**
		 * Populate the method field of the class box.
		 */
		methodText.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(final KeyEvent event) {
				editor.setMethods(methodText.getText());
			}
		});

		classBoxInput.getChildren().addAll(classText, attributeText, methodText);
		menuVBox.getChildren().addAll(menuLabel, classBoxInput, classButton, generalizationButton);

		/**
		 * Draw a generalization.
		 * 
		 * When mouse is pressed in the editor, set the starting X and Y
		 * coordinates. When the mouse is released, set the end point and draw
		 * the generalization.
		 */
		generalizationButton.setOnAction(new EventHandler<ActionEvent>() {

			double startX;
			double startY;
			double endX;
			double endY;

			@Override
			public void handle(ActionEvent generalizationSelected) {
				canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
					@Override

					/**
					 * Get starting X and Y coordinates from the mouse event.
					 */
					public void handle(MouseEvent mousePressed) {
						startX = mousePressed.getX();
						startY = mousePressed.getY();
					}
				});

				/**
				 * Get end X and Y coordinates from the mouse released event and
				 * draw the generalization.
				 */
				canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent mouseReleased) {

						endX = mouseReleased.getX();
						endY = mouseReleased.getY();
						view.getChildren().add(createGeneralization(startX, startY, endX, endY));
					}
				});
			}
		});

		classButton.setOnAction(new EventHandler<ActionEvent>() {

			/**
			 * Draws a class box using fields entered in the sidebar class box
			 * menu.
			 */
			@Override
			public void handle(ActionEvent event) {
				Node classBox = makeDraggable(
						createClassBoxLabel(editor.getName(), editor.getAttributes(), editor.getMethods()));
				view.getChildren().add(classBox);
			}
		});

		final Scene scene = new Scene(rootHBox, 1500, 750);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Creates a button.
	 * 
	 * @param name
	 *            Name of the button to be displayed.
	 * @param width
	 *            The width of the button.
	 * @return Button with specified name and width.
	 */
	public static Button createButton(String name, int width) {
		Button btn = new Button(name);
		btn.setPrefWidth(width);
		return btn;
	}

	/**
	 * Creates a generalization.
	 * 
	 * @param startX
	 *            The X coordinate returned from the initial mouse click.
	 * @param startY
	 *            The Y coordinate returned from the initial mouse click.
	 * @param endX
	 *            The X coordinate returned from the mouse release.
	 * @param endY
	 *            The Y coordinate returned from the mouse release.
	 * @return Node of a generalization which is a line and a polygon group.
	 */
	public Node createGeneralization(double startX, double startY, double endX, double endY) {

		Line line = new Line(startX, startY, endX, endY);
		Polygon arrowTip = new Polygon();
		arrowTip.getPoints()
				.addAll(new Double[] { (endX + 10), (endY), (endX - 10), (endY + 10), (endX - 10), (endY - 10) });
		arrowTip.setFill(Color.BLACK);
		arrowTip.setStroke(Color.BLACK);

		Group generalization = new Group(line, arrowTip);
		return generalization;
	}

	/**
	 * Creates a class box.
	 * 
	 * @param classString
	 *            The string used for the class' name.
	 * @param attributeString
	 *            The string used for the attributes field.
	 * @param methodString
	 *            The string used for the method field.
	 * @return Node of a class box.
	 */
	public Node createClassBoxLabel(String classString, String attributeString, String methodString) {
		VBox classBox = new VBox();
		classBox.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		classBox.setSpacing(5);
		classBox.setMinWidth(75);
		ClassBox box = new ClassBox(classString, attributeString, methodString);
		Separator separator1 = new Separator();
		Separator separator2 = new Separator();
		separator1.setMinWidth(Region.USE_PREF_SIZE);
		separator2.setMinWidth(Region.USE_PREF_SIZE);
		Label classText = new Label(box.getName());
		classText.setMinWidth(Region.USE_PREF_SIZE);
		classText.setAlignment(Pos.CENTER);
		Label attributeText = new Label(box.getAttributes());
		attributeText.setScaleShape(true);
		attributeText.setWrapText(true);
		attributeText.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		Label methodText = new Label(box.getMethods());
		methodText.setScaleShape(true);
		methodText.setWrapText(true);
		methodText.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		classBox.getChildren().addAll(classText, separator1, attributeText, separator2, methodText);

		classBox.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent mouseEvent) {
				if (mouseEvent.isControlDown()) {
					box.setName(editor.getName());
					classText.setText(box.getName());
					box.setAttributes(editor.getAttributes());
					attributeText.setText(box.getAttributes());
					box.setMethods(editor.getMethods());
					methodText.setText(box.getMethods());
				}
			}
		});

		return classBox;
	}

	/**
	 * Makes a node draggable.
	 * 
	 * @param node
	 *            Node which is going to be draggable.
	 * @return The draggable node.
	 */
	private Node makeDraggable(Node node) {
		final DragContext dragContext = new DragContext();
		final Group wrapGroup = new Group(node);

		wrapGroup.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent mouseEvent) {
				// remember initial mouse cursor coordinates
				// and node position
				dragContext.mouseOriginX = mouseEvent.getX();
				dragContext.mouseOriginY = mouseEvent.getY();
				dragContext.mouseTranslateX = node.getTranslateX();
				dragContext.mouseTranslateY = node.getTranslateY();
			}
		});

		wrapGroup.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent mouseEvent) {
				// shift node from its initial position by delta
				// calculated from mouse cursor movement
				node.setTranslateX(dragContext.mouseTranslateX + mouseEvent.getX() - dragContext.mouseOriginX);
				node.setTranslateY(dragContext.mouseTranslateY + mouseEvent.getY() - dragContext.mouseOriginY);
			}
		});

		return wrapGroup;
	}

	/**
	 * DragContext is used by MakeDraggable to find out how to move the intended
	 * box from its original/current position to a new position
	 */

	public static class DragContext {
		public double mouseOriginX;
		public double mouseOriginY;
		public double mouseTranslateX;
		public double mouseTranslateY;
	}

	public static void main(String[] args) {
		launch(args);
	}
}