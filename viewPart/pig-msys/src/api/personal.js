import myaxios from "../utils/myaxios";
import qs from "qs";

export default {
  getPersonalInfoById(id) {
    return myaxios({
      url: `/personalInfo/${id}`,
      method: 'get'
    })
  },
}
