package space.think.cloud.cts.collection.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import space.think.cloud.cts.collection.viewmodel.ProjectViewModel
import space.think.cloud.cts.lib.ui.project.ProjectContent
import space.think.cloud.cts.lib.ui.project.ProjectData


@Composable
fun ProjectScreen(
    modifier: Modifier,
    projectViewModel: ProjectViewModel = viewModel()
) {

    val projectList by projectViewModel.data.collectAsState()

    ProjectContent(
        modifier = modifier.fillMaxSize().padding(horizontal = 10.dp),
        items = listOf(
            ProjectData("123","123",0,0,),
            ProjectData("123","123",0,0,)
        )
    ) {

    }

}