package com.karateca;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.popup.AbstractPopup;

import java.awt.*;
import java.util.HashMap;

/**
 * @author Andres Dominguez.
 */
public class DDescirberAction extends AnAction {

    protected Project project;
    protected EditorImpl editor;
    protected AbstractPopup popup;
    protected VirtualFile virtualFile;
    protected DocumentImpl document;
    protected SearchBox searchBox;

    private Font font;
    private AceCanvas aceCanvas;
    private EditorColorsScheme scheme;

    private AceFinder aceFinder;
    private AceJumper aceJumper;

    private HashMap<String, Integer> textAndOffsetHash = new HashMap<String, Integer>();


    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(e.getData(PlatformDataKeys.EDITOR) != null);
    }

    public void actionPerformed(AnActionEvent actionEvent) {
        project = actionEvent.getData(PlatformDataKeys.PROJECT);
        editor = (EditorImpl) actionEvent.getData(PlatformDataKeys.EDITOR);
        virtualFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        document = (DocumentImpl) editor.getDocument();

        scheme = EditorColorsManager.getInstance().getGlobalScheme();
        font = new Font(scheme.getEditorFontName(), Font.BOLD, scheme.getEditorFontSize());

        aceFinder = new AceFinder(project, document, editor, virtualFile);
        aceJumper = new AceJumper(editor, document);

        aceCanvas = new AceCanvas();
        configureAceCanvas();

        searchBox = new SearchBox();
        configureSearchBox();
    }
}
