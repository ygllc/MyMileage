import { initializeApp } from "firebase/app";
import { getAuth, onAuthStateChanged } from "firebase/auth";
import { getFirestore } from "firebase/firestore";
import { getAnalytics } from "firebase/analytics";

const firebaseConfig = {
  // TODO: Paste your config object from Firebase Console -> Project Settings
  apiKey: null,
  authDomain: "kmpl-20bee.firebaseapp.com",
  projectId: "kmpl-20bee",
  storageBucket: null,
  messagingSenderId: "981449638816",
  appId: "1:981449638816:android:54d32a677b3f2e97d81ac5"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const db = getFirestore(app);

document.addEventListener('DOMContentLoaded', () => {
  const loadEl = document.querySelector('#load');

  if (loadEl) {
    loadEl.textContent = `Firebase SDK loaded with app: ${app.name}`;
  }

  // Example: Listen for auth state
  onAuthStateChanged(auth, (user) => {
    if (user) {
      console.log(`User signed in: ${user.uid}`);
    } else {
      console.log('No user signed in.');
    }
  });
});