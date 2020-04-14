package io.wastelesscorp.platform.app.controller;

import io.wastelesscorp.platform.app.security.controller.AuthenticationController;
import org.springframework.context.annotation.Import;

@Import({WeightedWasteController.class, AuthenticationController.class})
public class ControllerConfig {}
