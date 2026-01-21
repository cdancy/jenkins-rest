withConfig(configuration) {
  configuration.setDisabledGlobalASTTransformations(['groovy.grape.GrabAnnotationTransformation'] as Set)
}
