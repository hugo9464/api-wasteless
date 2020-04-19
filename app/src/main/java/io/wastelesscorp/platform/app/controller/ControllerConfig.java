package io.wastelesscorp.platform.app.controller;

import io.wastelesscorp.platform.app.controller.weightedwaste.WeightedWasteController;
import org.springframework.context.annotation.Import;

@Import(WeightedWasteController.class)
public class ControllerConfig {}
