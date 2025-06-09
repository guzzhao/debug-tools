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
package io.github.future0923.debug.tools.idea.startup;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 项目打开时调用
 *
 * @author future0923
 */
public class HotSwapStartupActivity implements ProjectActivity {

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        StateUtils.setProjectOpenTime(project);
        DebugToolsSettingState instance = DebugToolsSettingState.getInstance(project);
        String loadAgentPath = instance.loadAgentPath(project);
        if (StrUtil.isBlank(loadAgentPath)) {
            DebugToolsNotifierUtil.notifyError(project, "load agent path error");
        }
        return null;
    }
}
