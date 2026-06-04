import axios from 'axios';

axios.post('https://fit.newtype.design/api/auth/login', 'id=admin&pwd=1234qwer%21', { withCredentials: true }).catch(e => console.log(e)).then(r => console.log(r));