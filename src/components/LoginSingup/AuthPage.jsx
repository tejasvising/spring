import React, { useState,useEffect } from "react";
import { User, Mail, Lock, ArrowRight, LogIn } from "lucide-react";
import { signInWithPopup, GoogleAuthProvider, createUserWithEmailAndPassword, signInWithEmailAndPassword } from "firebase/auth";
import { auth, db } from "../../Firebase/Firebase";
import { doc, setDoc, getDoc } from "firebase/firestore";
import {login,logout} from "../../features/reducers/authReducer";
import { useLocation, useNavigate } from 'react-router-dom';

const AuthPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [isOld, setIsOld] = useState(true);
  const [formData, setFormData] = useState({ name: "", email: "", password: "" });
 
  // useEffect(() => {
  //   const hash = location.hash.substring(1); // Remove the #
  //   const params = new URLSearchParams(hash);
  //   const token = params.get('token');
  //   const email = params.get('email');
  //   console.log(token,email);
  //   if (token && email) {
  //     // Store token (in memory, localStorage, or context)
  //     localStorage.setItem('jwtToken', token);
      
  //     // Redirect to desired page
  //     navigate('/dashboard');
  //   } else {
  //     navigate('/login?error=oauth_failed');
  //   }
  // }, [location, navigate]);

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // Unified user document creation
  const createUserDocument = async (user, name = "") => {
    const userRef = doc(db, "users", user.uid);
    const userSnapshot = await getDoc(userRef);

    if (!userSnapshot.exists()) {
      await setDoc(userRef, {
        name: name || user.displayName || "User",
        email: user.email,
        userId: user.uid,
        createdAt: new Date().toISOString(),
      });
    }
  };

  // Email/Password Authentication
  const handleEmailRegister = async (e) => {
    e.preventDefault();
    try {
      console.log("json: ",JSON.stringify({
        "email": formData.email,
        "password": formData.password,
        "name": formData.name
      }),);
      const res=await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          "email": formData.email,
          "password": formData.password,
          "name": formData.name
        }),
      });
      const data= await res.text();
      console.log("register api data: ",data.userid);
      if(data=='Registered successfully'){
        localStorage.setItem('userid', data.userid);
        //localStorage.setItem('token', data.token);
      }
    }
      catch(e){
        console.log(e.message);
      }
    }
  const handleEmailAuth = async (e) => {
   e.preventDefault();
    try {
      console.log("json: ",JSON.stringify({
        "email": formData.email,
        "password": formData.password
      }),);
      const res=await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          "email": formData.email,
          "password": formData.password
        }),
      });
      const data= await res.json();
      console.log("User signed in:", data.userid);
     localStorage.setItem('userid', data.userid);
     // localStorage.setItem('token', data.token);
     // console.log("login api data: ",data);
      let userCredential;
      if (data=="User not found") {
        //userCredential = await signInWithEmailAndPassword(auth, formData.email, formData.password);
        setIsOld(false);
        alert("User not found, please register first.");
       /*await fetch('http://127.0.0.1:8080/api/auth/register', {
        method: 'POST',
        body: JSON.stringify({
          "email": formData.email,
          "password": formData.password
        }),
      });*/
      
      } else {
       // userCredential = await createUserWithEmailAndPassword(auth, formData.email, formData.password);
        //await createUserDocument(userCredential.user, formData.name);
        
      }
     
     
     // setIsLogin(true);
      navigate('/select');
    //  alert(`Welcome, ${userCredential.user.email}!`);
    } catch (error) {
      
      alert(error.message.replace("Firebase: ", ""));
    }
  };

  // Google Authentication
  const handleGoogleAuth = async (e) => {
    e.preventDefault();
    try {
      

          
          window.location.href = 'http://localhost:8080/oauth2/authorization/google';
      
    } catch (error) {
      
      
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-lg p-6">
        <div className="text-center">
          <h2 className="text-2xl font-bold">{isOld ? "Welcome Back!" : "Create Account"}</h2>
          <p className="text-gray-500 mt-1">
            {isOld ? "Sign in to continue" : "Get started with your account"}
          </p>
        </div>

        <form  className="mt-6 space-y-4">
          {!isOld && (
            <div className="flex items-center border border-gray-300 rounded-lg px-3 py-2">
              <User className="w-5 h-5 text-gray-500" />
              <input
                type="text"
                name="name"
                placeholder="Full Name"
                value={formData.name}
                onChange={handleInputChange}
                className="ml-2 w-full outline-none bg-transparent"
                required
              />
            </div>
          )}

          <div className="flex items-center border border-gray-300 rounded-lg px-3 py-2">
            <Mail className="w-5 h-5 text-gray-500" />
            <input
              type="email"
              name="email"
              placeholder="Email Address"
              value={formData.email}
              onChange={handleInputChange}
              className="ml-2 w-full outline-none bg-transparent"
              required
            />
          </div>

          <div className="flex items-center border border-gray-300 rounded-lg px-3 py-2">
            <Lock className="w-5 h-5 text-gray-500" />
            <input
              type="password"
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleInputChange}
              className="ml-2 w-full outline-none bg-transparent"
              required
              minLength="6"
            />
          </div>

          <button
           
            onClick={isOld?handleEmailAuth:handleEmailRegister}
            className="w-full flex justify-center items-center bg-blue-600 text-white font-semibold rounded-lg py-2.5 hover:bg-blue-700 transition-colors"
          >
            {isOld ? "Continue to Account" : "Create Account"}
            <ArrowRight className="w-4 h-4 ml-2" />
          </button>
        </form>

        <div className="mt-6">
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-gray-300"></div>
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="px-2 bg-white text-gray-500">Or continue with</span>
            </div>
          </div>

          <div className="mt-4 space-y-3">
            <button
              onClick={handleGoogleAuth}
              className="w-full flex items-center justify-center bg-gray-100 text-gray-700 font-semibold rounded-lg py-2.5 hover:bg-gray-200 transition-colors border border-gray-300"
            >
              <LogIn className="w-4 h-4 mr-2" />
              {isOld ? "Google Sign In" : "Google Sign Up"}
            </button>

            <p className="text-center text-sm text-gray-500 mt-4">
              {isOld ? "Don't have an account?" : "Already have an account?"}
              <button
                onClick={() => setIsOld(!isOld)}
                className="ml-1.5 text-blue-600 font-semibold hover:text-blue-700"
              >
                {isOld ? "Sign up" : "Login"}
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;
