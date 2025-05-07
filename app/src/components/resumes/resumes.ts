import { Component } from 'vue'
// Resume components registration
import MaterialDark from './material-dark.vue'
import LeftRight from './left-right.vue'
import Oblique from './oblique.vue'
import SideBar from './side-bar.vue'
import Purple from './purple.vue'
import SideBarRtl from './side-bar-rtl.vue'
import LeftRightRtl from './left-right-rtl.vue'
import ObliqueRtl from './oblique-rtl.vue'
import Creative from './creative.vue'
import Cool from './cool.vue'
import CoolRtl from './cool-rtl.vue'
import CoolRtl2 from './cool-rtl2.vue'
import Green from './green.vue'
import LeftRightProjects from './left-right-projects.vue'
import MaterialDarkProjects from './material-dark-projects.vue'
import ObliqueProjects from './oblique-projects.vue'
import SideBarProjects from './side-bar-projects.vue'

export const resumeComponents: Record<string, Component> = {
  'material-dark': MaterialDark,
  'left-right': LeftRight,
  'oblique': Oblique,
  'side-bar': SideBar,
  'purple': Purple,
  'side-bar-rtl': SideBarRtl,
  'left-right-rtl': LeftRightRtl,
  'oblique-rtl': ObliqueRtl,
  'creative': Creative,
  'cool': Cool,
  'cool-rtl': CoolRtl,
  'cool-rtl2': CoolRtl2,
  'green': Green,
  'left-right-projects': LeftRightProjects,
  'material-dark-projects': MaterialDarkProjects,
  'oblique-projects': ObliqueProjects,
  'side-bar-projects': SideBarProjects
} 