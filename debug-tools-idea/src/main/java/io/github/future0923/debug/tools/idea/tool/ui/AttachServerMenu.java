/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.idea.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextArea;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.model.ServerDisplayValue;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsAttachUtils;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author future0923
 */
public class AttachServerMenu extends JBPopupMenu {

    private final List<JBRadioButton> radioButtonList = new ArrayList<>();

    private final JPanel radioPanel = new JPanel();

    public AttachServerMenu(Project project) {
        super();
        this.setLayout(new BorderLayout());
        initToolbar(project);
    }

    private void initToolbar(Project project) {
        radioPanel.setMinimumSize(new Dimension(500, 100));
        initVmServer();
        JPanel buttonPane = new JPanel();
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> this.setVisible(false));
        buttonPane.add(cancel);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> {
            radioPanel.removeAll();
            initVmServer();
        });
        buttonPane.add(refresh);
        JButton attach = new JButton("Attach");
        attach.addActionListener(e -> radioButtonList.stream().filter(AbstractButton::isSelected).findFirst().ifPresent(button -> {
            ServerDisplayValue serverDisplayValue = ServerDisplayValue.of(button.getText());
            if (serverDisplayValue != null) {
                DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
                String agentPath = settingState.getAgentPath();
                if (StrUtil.isBlank(agentPath)) {
                    DebugToolsNotifierUtil.notifyError(project, "load agent path error");
                    return;
                }
                DebugToolsAttachUtils.attachLocal(project, serverDisplayValue.getKey(), serverDisplayValue.getValue(), agentPath);
                settingState.setLocal(true);
            }
            this.setVisible(false);
        }));
        buttonPane.add(attach);
        this.add(radioPanel, BorderLayout.CENTER);
        this.add(buttonPane, BorderLayout.SOUTH);
    }

    private void initVmServer() {
        ButtonGroup radioGroup = new ButtonGroup();
        DebugToolsAttachUtils.vmConsumer(size -> {
                    if (size == 0) {
                        JBTextArea textArea = new JBTextArea("No server found");
                        textArea.setEnabled(false);
                        radioPanel.add(textArea);
                    } else {
                        radioPanel.setLayout(new GridLayout(size, 1, 3, 3));
                    }
                },
                descriptor -> {
                    JBRadioButton radioButton = new JBRadioButton(ServerDisplayValue.display(descriptor.id(), descriptor.displayName()));
                    radioPanel.add(radioButton);
                    radioGroup.add(radioButton);
                    radioButtonList.add(radioButton);
                });
    }
}
