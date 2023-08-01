import axios from 'axios'
import type { User } from '@/types/User'
import { useToast } from 'vue-toast-notification'

export default class UserService {

  public static async getUserDetails() {
    const response = await axios
      .get<User>('api/user/me')
    return response.data
  }

  public static handleNotAuthenticatedUser(){
    const $toast = useToast()
    $toast.open({
      message: 'Please login first!',
      type: 'error',
    });
  }

}
