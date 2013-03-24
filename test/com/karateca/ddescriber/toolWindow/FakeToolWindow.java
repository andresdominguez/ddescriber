package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowContentUiType;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentManagerListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class FakeToolWindow implements ToolWindow {

  private Content content;

  public Content getContent() {
    return content;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public void activate(@Nullable Runnable runnable) {

  }

  @Override
  public void activate(@Nullable Runnable runnable, boolean autoFocusContents) {

  }

  @Override
  public void activate(@Nullable Runnable runnable, boolean autoFocusContents, boolean forced) {

  }

  @Override
  public boolean isVisible() {
    return false;
  }

  @Override
  public void show(@Nullable Runnable runnable) {

  }

  @Override
  public void hide(@Nullable Runnable runnable) {

  }

  @Override
  public ToolWindowAnchor getAnchor() {
    return null;
  }

  @Override
  public void setAnchor(ToolWindowAnchor anchor, @Nullable Runnable runnable) {

  }

  @Override
  public boolean isSplitMode() {
    return false;
  }

  @Override
  public void setSplitMode(boolean split, @Nullable Runnable runnable) {

  }

  @Override
  public boolean isAutoHide() {
    return false;
  }

  @Override
  public void setAutoHide(boolean state) {

  }

  @Override
  public ToolWindowType getType() {
    return null;
  }

  @Override
  public void setType(ToolWindowType type, @Nullable Runnable runnable) {

  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public void setIcon(Icon icon) {

  }

  @Override
  public String getTitle() {
    return null;
  }

  @Override
  public void setTitle(String title) {

  }

  @Override
  public boolean isAvailable() {
    return false;
  }

  @Override
  public void setAvailable(boolean available, @Nullable Runnable runnable) {

  }

  @Override
  public void setContentUiType(ToolWindowContentUiType type, @Nullable Runnable runnable) {

  }

  @Override
  public void setDefaultContentUiType(@NotNull ToolWindowContentUiType type) {

  }

  @Override
  public ToolWindowContentUiType getContentUiType() {
    return null;
  }

  @Override
  public void installWatcher(ContentManager contentManager) {

  }

  @Override
  public JComponent getComponent() {
    return null;
  }

  @Override
  public ContentManager getContentManager() {
    return new ContentManager() {

      @Override
      public boolean canCloseContents() {
        return false;
      }

      @Override
      public JComponent getComponent() {
        return null;
      }

      @Override
      public void addContent(@NotNull Content content2) {
        content = content2;
      }

      @Override
      public void addContent(@NotNull Content content, int order) {

      }

      @Override
      public void addContent(@NotNull Content content, Object constraints) {

      }

      @Override
      public boolean removeContent(@NotNull Content content, boolean dispose) {
        return false;
      }

      @Override
      public ActionCallback removeContent(@NotNull Content content, boolean dispose, boolean trackFocus, boolean forcedFocus) {
        return null;
      }

      @Override
      public void setSelectedContent(@NotNull Content content) {

      }

      @Override
      public ActionCallback setSelectedContentCB(@NotNull Content content) {
        return null;
      }

      @Override
      public void setSelectedContent(@NotNull Content content, boolean requestFocus) {

      }

      @Override
      public ActionCallback setSelectedContentCB(@NotNull Content content, boolean requestFocus) {
        return null;
      }

      @Override
      public void setSelectedContent(@NotNull Content content, boolean requestFocus, boolean forcedFocus) {

      }

      @Override
      public ActionCallback setSelectedContentCB(@NotNull Content content, boolean requestFocus, boolean forcedFocus) {
        return null;
      }

      @Override
      public ActionCallback setSelectedContent(@NotNull Content content, boolean requestFocus, boolean forcedFocus, boolean implicit) {
        return null;
      }

      @Override
      public void addSelectedContent(@NotNull Content content) {

      }

      @Nullable
      @Override
      public Content getSelectedContent() {
        return null;
      }

      @NotNull
      @Override
      public Content[] getSelectedContents() {
        return new Content[0];
      }

      @Override
      public void removeAllContents(boolean dispose) {

      }

      @Override
      public int getContentCount() {
        return 0;
      }

      @NotNull
      @Override
      public Content[] getContents() {
        return new Content[0];
      }

      @Override
      public Content findContent(String displayName) {
        return null;
      }

      @Nullable
      @Override
      public Content getContent(int index) {
        return null;
      }

      @Override
      public Content getContent(JComponent component) {
        return null;
      }

      @Override
      public int getIndexOfContent(Content content) {
        return 0;
      }

      @Override
      public String getCloseActionName() {
        return null;
      }

      @Override
      public boolean canCloseAllContents() {
        return false;
      }

      @Override
      public ActionCallback selectPreviousContent() {
        return null;
      }

      @Override
      public ActionCallback selectNextContent() {
        return null;
      }

      @Override
      public void addContentManagerListener(@NotNull ContentManagerListener l) {

      }

      @Override
      public void removeContentManagerListener(@NotNull ContentManagerListener l) {

      }

      @Override
      public String getCloseAllButThisActionName() {
        return null;
      }

      @Override
      public String getPreviousContentActionName() {
        return null;
      }

      @Override
      public String getNextContentActionName() {
        return null;
      }

      @Override
      public List<AnAction> getAdditionalPopupActions(@NotNull Content content) {
        return null;
      }

      @Override
      public void removeFromSelection(@NotNull Content content) {

      }

      @Override
      public boolean isSelected(@NotNull Content content) {
        return false;
      }

      @Override
      public ActionCallback requestFocus(@Nullable Content content, boolean forced) {
        return null;
      }

      @Override
      public void addDataProvider(@NotNull DataProvider provider) {

      }

      @NotNull
      @Override
      public ContentFactory getFactory() {
        return null;
      }

      @Override
      public boolean isDisposed() {
        return false;
      }

      @Override
      public boolean isSingleSelection() {
        return false;
      }

      @Override
      public ActionCallback getReady(@NotNull Object requestor) {
        return null;
      }

      @Override
      public void dispose() {

      }
    };
  }

  @Override
  public void setDefaultState(@Nullable ToolWindowAnchor anchor, @Nullable ToolWindowType type, @Nullable Rectangle floatingBounds) {

  }

  @Override
  public void setToHideOnEmptyContent(boolean hideOnEmpty) {

  }

  @Override
  public boolean isToHideOnEmptyContent() {
    return false;
  }

  @Override
  public boolean isDisposed() {
    return false;
  }

  @Override
  public void showContentPopup(InputEvent inputEvent) {

  }

  @Override
  public ActionCallback getActivation() {
    return null;
  }

  @Override
  public ActionCallback getReady(@NotNull Object requestor) {
    return null;
  }
}
